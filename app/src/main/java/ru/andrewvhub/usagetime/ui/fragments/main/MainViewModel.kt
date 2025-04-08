package ru.andrewvhub.usagetime.ui.fragments.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.highlight.Highlight
import com.google.gson.Gson
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.core.BaseViewModel
import ru.andrewvhub.usagetime.core.launchSafe
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsage
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsageApp
import ru.andrewvhub.usagetime.domain.useCase.GetUsageStatAppsByPeriodUseCase
import ru.andrewvhub.usagetime.ui.items.DailyUsageItem
import ru.andrewvhub.utils.SingleLiveEvent
import ru.andrewvhub.utils.WeekNavigator
import ru.andrewvhub.utils.extension.formatTime
import ru.andrewvhub.utils.extension.getIndexDayOfWeekNumberFromTimestamp
import ru.andrewvhub.utils.extension.toDateString
import java.io.File
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainViewModel(
    private val getUsageStatAppsByPeriodUseCase: GetUsageStatAppsByPeriodUseCase,
    private val cacheDir: File?,
) : BaseViewModel() {

    private val weekNavigator = WeekNavigator()

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _dailyUsageList = MutableLiveData<List<BarEntry>>()
    val dailyUsageList: LiveData<List<BarEntry>> = _dailyUsageList

    private val _highlightLiveData = MutableLiveData<Highlight>()
    val highlightLiveData: LiveData<Highlight> = _highlightLiveData

    private val _totalTimeByDayLiveData = MutableLiveData<String>()
    val totalTimeByDayLiveData: LiveData<String> = _totalTimeByDayLiveData

    private val _usageForPeriodLiveData = MutableLiveData<List<DailyUsage>>()

    private val _dailyUsageForPeriodLiveData = MutableLiveData<List<DailyUsageItem>>()
    val dailyUsageForPeriodLiveData: LiveData<List<DailyUsageItem>> = _dailyUsageForPeriodLiveData

    private val _currentDateMsLiveData = MutableLiveData<Long>()
    val currentDateMsLiveData: LiveData<Long> = _currentDateMsLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        refreshData()
    }

    private fun getData(startTime: Long, endTime: Long) {
        launchSafe(
            start = { _isLoading.postValue(true) },
            body = {
                val result = getUsageStatAppsByPeriodUseCase.invoke(
                    GetUsageStatAppsByPeriodUseCase.Param(
                        startDate = startTime,
                        endDate = endTime
                    )
                )
                result.last().let { lastDay ->
                    _currentDateMsLiveData.postValue(lastDay.dateMs)
                    _totalTimeByDayLiveData.postValue(lastDay.totalTimeMs.formatTime(resources))
                    _dailyUsageForPeriodLiveData.postValue(lastDay.appUsageDetails.sortedBy { it.usageMs }
                        .map { it.toItem() }.reversed())
                }
                _usageForPeriodLiveData.postValue(result)

                val lastActiveDateHighlight =
                    result.last().dateMs.getIndexDayOfWeekNumberFromTimestamp()
                getOneDayUsageByDay(lastActiveDateHighlight)
                _highlightLiveData.postValue(
                    Highlight(
                        lastActiveDateHighlight.toFloat(),
                        0f,
                        0
                    )
                )
                _dailyUsageList.postValue(result.createUsageHoursEntries())
            },
            onError = { _errorMessage.postValue(it.handleThrowable()) },
            final = { _isLoading.postValue(false) }
        )
    }

    fun getOneDayUsageByDay(indexByDay: Int) {
        _usageForPeriodLiveData.value?.findLast {
            it.dateMs.getIndexDayOfWeekNumberFromTimestamp() == indexByDay
        }?.let { oneDayUsageInfo ->
            _totalTimeByDayLiveData.postValue(oneDayUsageInfo.totalTimeMs.formatTime(resources))
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

    fun createJsonFromListAndSave(): File? {
        _usageForPeriodLiveData.value?.let {
            val file = exportDataToJsonFile(
                fileName = createFileNameFromMS(it.first().dateMs, it.last().dateMs),
                data = it
            )
            return file
        }
        return null
    }

    fun refreshData() {
        val currentWeekRange = weekNavigator.getCurrentWeekRange()
        getData(currentWeekRange.first, currentWeekRange.second)
    }

    private fun List<DailyUsage>.createUsageHoursEntries(): List<BarEntry> {
        val entries = arrayListOf<BarEntry>()
        val indexDaysWeek = mutableSetOf(0, 1, 2, 3, 4, 5, 6)
        this.forEach { dailyUsage ->
            val dayOfWeekNumber = dailyUsage.dateMs.getIndexDayOfWeekNumberFromTimestamp()
            indexDaysWeek.remove(dayOfWeekNumber)
            val usageHours = dailyUsage.totalTimeMs / 3_600_000f
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
            mainNavigate(
                MainFragmentDirections.actionMainFragmentToDetailUsageStatAppFragment(
                    packageName = packageName
                )
            )
        }
    )

    //Что кайф можно экспортить в файл, не стал привязывать к конкретному классу
    private fun <T> exportDataToJsonFile(fileName: String, data: List<T>): File? {
        runCatching {
            val gson = Gson()
            val jsonString = gson.toJson(data)
            val file = File(cacheDir, fileName)
            file.writeText(jsonString)
            return file
        }.getOrElse { return null }
    }

    private fun createFileNameFromMS(startDate: Long, endDate: Long): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd_MM_yyyy", Locale.getDefault())
        return resources.getString(
            R.string.main_fragment_file_name_date_from_to_format,
            startDate.toDateString(dateFormatter),
            endDate.toDateString(dateFormatter)
        )
    }
}