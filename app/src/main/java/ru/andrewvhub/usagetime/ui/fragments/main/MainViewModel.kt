package ru.andrewvhub.usagetime.ui.fragments.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.highlight.Highlight
import ru.andrewvhub.usagetime.core.BaseViewModel
import ru.andrewvhub.usagetime.core.launchSafe
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsage
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsageApp
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsageAppByWeekDay
import ru.andrewvhub.usagetime.data.models.usageModels.toModel
import ru.andrewvhub.usagetime.domain.useCase.GetUsageStatAppByPeriodUseCase
import ru.andrewvhub.usagetime.ui.items.DailyUsageItem
import ru.andrewvhub.utils.SingleLiveEvent
import ru.andrewvhub.utils.WeekNavigator
import ru.andrewvhub.utils.extension.formatTime
import ru.andrewvhub.utils.extension.getIndexDayOfWeekNumberFromTimestamp
import ru.andrewvhub.utils.extension.toDateString

class MainViewModel(
    private val getUsageStatAppByPeriodUseCase: GetUsageStatAppByPeriodUseCase
) : BaseViewModel() {

    private val _dailyUsageList = SingleLiveEvent<List<BarEntry>>()
    val dailyUsageList: LiveData<List<BarEntry>> = _dailyUsageList

    private val _highlightLiveData = SingleLiveEvent<Highlight>()
    val highlightLiveData: LiveData<Highlight> = _highlightLiveData

    private val _totalTimeByDayLiveData = SingleLiveEvent<String>()
    val totalTimeByDayLiveData: LiveData<String> = _totalTimeByDayLiveData

    private val _usageForPeriodLiveData = MutableLiveData<List<DailyUsageAppByWeekDay>>()

    private val _dailyUsageForPeriodLiveData = SingleLiveEvent<List<DailyUsageItem>>()
    val dailyUsageForPeriodLiveData: LiveData<List<DailyUsageItem>> = _dailyUsageForPeriodLiveData

    private val _currentDateMsLiveData = SingleLiveEvent<Long>()
    val currentDateMsLiveData: LiveData<Long> = _currentDateMsLiveData

    private val _isLoading = SingleLiveEvent<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val currentWeekRange = WeekNavigator.getCurrentWeekRange()
        getData(currentWeekRange.first, currentWeekRange.second)
    }

    private fun getData(startTime: Long, endTime: Long) {
        launchSafe(
            start = {
                _isLoading.postValue(true)
                Log.d("OS4:ViewModel", "Start")
            },
            body = {
                val value = getUsageStatAppByPeriodUseCase.invoke(
                    GetUsageStatAppByPeriodUseCase.Param(
                        startDate = startTime,
                        endDate = endTime
                    )
                )

                _usageForPeriodLiveData.postValue(value.map { it.toModel() })

                value.forEach { Log.d("OS4:ViewModel", it.dateMs.toString()) }

                val lastActiveDateHighlight =
                    value.last().dateMs.getIndexDayOfWeekNumberFromTimestamp()
                getOneDayUsageByDay(lastActiveDateHighlight)
                _highlightLiveData.postValue(
                    Highlight(
                        lastActiveDateHighlight.toFloat(),
                        0f,
                        0
                    )
                )
                _dailyUsageList.postValue(value.createUsageHoursEntries())
            },
            onError = {
                Log.e("OS4:ViewModel", it.toString())
            },
            final = {
                _isLoading.postValue(false)
            }
        )
    }

    fun getOneDayUsageByDay(indexByDay: Int) {
        _usageForPeriodLiveData.value?.findLast {
            it.indexDayOfWeek == indexByDay
        }?.let { oneDayUsageInfo ->
            _totalTimeByDayLiveData.postValue(oneDayUsageInfo.totalUsageTime.formatTime(resources))
            _currentDateMsLiveData.postValue(oneDayUsageInfo.dateMs)
            val index = _usageForPeriodLiveData.value?.indexOf(oneDayUsageInfo)
            index?.let {
                val list = oneDayUsageInfo.appUsageDetails
                _dailyUsageForPeriodLiveData.postValue(list.sortedBy { it.usageMs }
                    .map { it.toItem() }.reversed())
            }
        }
    }

    fun getPreviousWeekRange() {
        val currentWeekRange = WeekNavigator.getCurrentWeekRange()
        val previousWeekRange = WeekNavigator.getPreviousWeekRange()

        if (previousWeekRange.first != currentWeekRange.first)
            getData(previousWeekRange.first, previousWeekRange.second)
    }

    fun getNextWeekRange() {
        val currentWeekRange = WeekNavigator.getCurrentWeekRange()
        val nextWeekRange = WeekNavigator.getNextWeekRange()
        if (nextWeekRange.first != currentWeekRange.first)
            getData(nextWeekRange.first, nextWeekRange.second)
    }

    private fun List<DailyUsage>.createUsageHoursEntries(): List<BarEntry> {
        val entries = arrayListOf<BarEntry>()
        val indexDaysWeek = mutableSetOf(0, 1, 2, 3, 4, 5, 6)
        this.forEach { dailyUsage ->
            val dayOfWeekNumber = dailyUsage.dateMs.getIndexDayOfWeekNumberFromTimestamp()
            indexDaysWeek.remove(dayOfWeekNumber)
            val usageHours = dailyUsage.totalTimeMs / 3_600_000f

            Log.d("OS4:MainViewModel", "День: ${dailyUsage.dateMs.toDateString()} usageHours: $usageHours")
            entries.add(BarEntry(dayOfWeekNumber.toFloat(), usageHours))
        }
        if (indexDaysWeek.isNotEmpty()) {
            indexDaysWeek.forEach { index ->
                entries.add(BarEntry(index.toFloat(), 0f))
            }
        }
        return entries.sortedBy { it.x }
    }

    private fun DailyUsageApp.toItem(): DailyUsageItem = DailyUsageItem(
        appIcon = icon,
        appName = appName,
        appUsageTime = usageMs.formatTime(resources),
        onClick = {
            Log.d("OS4:DailyUsageAppViewHolder", "onClick")
        }
    )
}