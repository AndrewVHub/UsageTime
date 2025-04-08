package ru.andrewvhub.utils

import java.util.Calendar
import java.util.Locale

// Объект для управления неделями
class WeekNavigator {

    private var currentTime: Long

    init {
        val calendar = getCalendarInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.setToStartOfDay()
        currentTime = calendar.timeInMillis
    }

    private fun getCalendarInstance(): Calendar {
        return Calendar.getInstance(Locale.getDefault()).apply {
            firstDayOfWeek = Calendar.MONDAY
        }
    }

    private fun getStartOfWeek(calendar: Calendar): Long {
        val c = calendar.clone() as Calendar
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        c.setToStartOfDay()
        return c.timeInMillis
    }

    private fun getEndOfWeek(calendar: Calendar): Long {
        val c = calendar.clone() as Calendar
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        c.setToEndOfDay()
        return c.timeInMillis
    }

    private fun Calendar.setToStartOfDay() {
        this.set(Calendar.HOUR_OF_DAY, 0)
        this.set(Calendar.MINUTE, 0)
        this.set(Calendar.SECOND, 0)
        this.set(Calendar.MILLISECOND, 0)
    }

    private fun Calendar.setToEndOfDay() {
        this.set(Calendar.HOUR_OF_DAY, 23)
        this.set(Calendar.MINUTE, 59)
        this.set(Calendar.SECOND, 59)
        this.set(Calendar.MILLISECOND, 999)
    }

    private fun getStartOfPreviousActualWeek(): Long {
        val calendar = getCalendarInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.setToStartOfDay()
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        return calendar.timeInMillis
    }

    // Получить диапазон предыдущей недели (стрелка влево)
    fun getPreviousWeekRange(): Pair<Long, Long> {
        val currentStart = currentTime
        val calendar = getCalendarInstance().apply {
            timeInMillis = currentStart
        }
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val previousStart = getStartOfWeek(calendar)
        val previousEnd = getEndOfWeek(calendar)

        val previousActualWeekStart = getStartOfPreviousActualWeek()

        return if (previousStart < previousActualWeekStart) {
            getCurrentWeekRange() // Возвращаем текущий диапазон, если попытка уйти дальше, чем на 1 неделю назад от текущей
        } else if (previousStart == currentStart) {
            getCurrentWeekRange() // Возвращаем текущий диапазон, если нет изменений
        } else {
            currentTime = previousStart
            Pair(previousStart, previousEnd)
        }
    }

    // Получить диапазон следующей недели (стрелка вправо)
    fun getNextWeekRange(): Pair<Long, Long> {
        val calendar = getCalendarInstance().apply {
            timeInMillis = currentTime
        }
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val nextWeekStart = getStartOfWeek(calendar)
        calendar.setToEndOfDay()
        val nextWeekEnd = calendar.timeInMillis

        // Получаем начало фактической текущей недели
        val currentCalendar = getCalendarInstance()
        currentCalendar.firstDayOfWeek = Calendar.MONDAY
        currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        currentCalendar.setToStartOfDay()
        val actualCurrentWeekStart = currentCalendar.timeInMillis

        // Если начало следующей недели не позже начала фактической текущей недели,
        // то мы переходим дальше
        if (nextWeekStart <= actualCurrentWeekStart) {
            currentTime = nextWeekStart
            return Pair(nextWeekStart, minOf(nextWeekEnd, System.currentTimeMillis()))
        } else {
            // Возвращаем диапазон текущей отображаемой недели, если попытка перейти дальше текущей
            val currentViewedCalendar = getCalendarInstance().apply {
                timeInMillis = currentTime
            }
            val currentViewedStart = getStartOfWeek(currentViewedCalendar)
            currentViewedCalendar.setToEndOfDay()
            return Pair(currentViewedStart, minOf(currentViewedCalendar.timeInMillis, System.currentTimeMillis()))
        }
    }

    // Получить диапазон текущей недели
    fun getCurrentWeekRange(): Pair<Long, Long> {
        val calendar = getCalendarInstance().apply {
            timeInMillis = currentTime
        }
        val startOfWeek = getStartOfWeek(calendar)
        val endOfWeekPotential = getEndOfWeek(calendar)
        val now = System.currentTimeMillis()
        val endOfWeek = minOf(endOfWeekPotential, now)
        return startOfWeek to endOfWeek
    }
}