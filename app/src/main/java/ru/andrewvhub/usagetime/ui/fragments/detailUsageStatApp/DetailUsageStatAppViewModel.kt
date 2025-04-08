package ru.andrewvhub.usagetime.ui.fragments.detailUsageStatApp

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.highlight.Highlight
import ru.andrewvhub.usagetime.core.BaseViewModel
import ru.andrewvhub.usagetime.core.launchSafe
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsageApp
import ru.andrewvhub.usagetime.domain.useCase.GetUsageStatByNameAppUseCase
import ru.andrewvhub.utils.SingleLiveEvent
import ru.andrewvhub.utils.WeekNavigator
import ru.andrewvhub.utils.extension.formatTime
import ru.andrewvhub.utils.extension.getIndexDayOfWeekNumberFromTimestamp

class DetailUsageStatAppViewModel(
    private val packageNameArg: String,
    private val getUsageStatByNameAppUseCase: GetUsageStatByNameAppUseCase
) : BaseViewModel() {

    private val weekNavigator = WeekNavigator()

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoadingLiveData = SingleLiveEvent<Boolean>()
    val isLoadingLiveData: LiveData<Boolean> = _isLoadingLiveData

    private val _highlightLiveData = MutableLiveData<Highlight>()
    val highlightLiveData: LiveData<Highlight> = _highlightLiveData

    private val _appImageLiveData = MutableLiveData<Drawable>()
    val appImageLiveData: LiveData<Drawable> = _appImageLiveData

    private val _dailyUsageBarEntryList = MutableLiveData<List<BarEntry>>()
    val dailyUsageBarEntryList: LiveData<List<BarEntry>> = _dailyUsageBarEntryList

    private val _dailyUsageList = MutableLiveData<List<DailyUsageApp>>()

    private val _totalTimeByDayLiveData = MutableLiveData<String>()
    val totalTimeByDayLiveData: LiveData<String> = _totalTimeByDayLiveData

    private val _currentDateMsLiveData = MutableLiveData<Long>()
    val currentDateMsLiveData: LiveData<Long> = _currentDateMsLiveData


    init {
        refreshData()
    }

    private fun getData(startTime: Long, endTime: Long) {
        launchSafe(
            start = { _isLoadingLiveData.postValue(true) },
            body = {
                val value = getUsageStatByNameAppUseCase.invoke(
                    GetUsageStatByNameAppUseCase.Param(
                        packageNameArg,
                        startTime,
                        endTime
                    )
                )
                _dailyUsageList.postValue(value)
                _dailyUsageBarEntryList.postValue(value.createUsageHoursEntries())

                value.first().icon?.let { _appImageLiveData.postValue(it) }
                value.last().let { lastDay ->
                    val lastActiveDateHighlight = lastDay.dateMs.getIndexDayOfWeekNumberFromTimestamp()
                    _currentDateMsLiveData.postValue(lastDay.dateMs)
                    _totalTimeByDayLiveData.postValue(lastDay.usageMs.formatTime(resources))
                    getOneDayUsageByDay(lastActiveDateHighlight)
                    _highlightLiveData.postValue(
                        Highlight(
                            lastActiveDateHighlight.toFloat(),
                            0f,
                            0
                        )
                    )
                }
            },
            onError = { _errorMessage.postValue(it.handleThrowable()) },
            final = { _isLoadingLiveData.postValue(false) }
        )
    }

    fun getOneDayUsageByDay(indexByDay: Int) {
        _dailyUsageList.value?.findLast {
            it.dateMs.getIndexDayOfWeekNumberFromTimestamp() == indexByDay
        }?.let { oneDayUsageInfo ->
            _totalTimeByDayLiveData.postValue(oneDayUsageInfo.usageMs.formatTime(resources))
            _currentDateMsLiveData.postValue(oneDayUsageInfo.dateMs)
        }
    }

    fun getPreviousWeekRange() {
        val currentWeekRange = weekNavigator.getCurrentWeekRange()
        val previousWeekRange = weekNavigator.getPreviousWeekRange()

        if (previousWeekRange.first != currentWeekRange.first)
            getData(previousWeekRange.first, previousWeekRange.second)
    }

    fun getNextWeekRange() {
        val currentWeekRange = weekNavigator.getCurrentWeekRange()
        val nextWeekRange = weekNavigator.getNextWeekRange()
        if (nextWeekRange.first != currentWeekRange.first)
            getData(nextWeekRange.first, nextWeekRange.second)
    }

    fun refreshData() {
        val currentWeekRange = weekNavigator.getCurrentWeekRange()
        getData(currentWeekRange.first, currentWeekRange.second)
    }

    private fun List<DailyUsageApp>.createUsageHoursEntries(): List<BarEntry> {
        val entries = arrayListOf<BarEntry>()
        val indexDaysWeek = mutableSetOf(0, 1, 2, 3, 4, 5, 6)
        this.forEach { dailyUsage ->
            val dayOfWeekNumber = dailyUsage.dateMs.getIndexDayOfWeekNumberFromTimestamp()
            indexDaysWeek.remove(dayOfWeekNumber)
            val usageHours = dailyUsage.usageMs / 3_600_000f
            entries.add(BarEntry(dayOfWeekNumber.toFloat(), usageHours))
        }
        if (indexDaysWeek.isNotEmpty()) {
            indexDaysWeek.forEach { index ->
                entries.add(BarEntry(index.toFloat(), 0f))
            }
        }
        return entries.sortedBy { it.x }
    }
}