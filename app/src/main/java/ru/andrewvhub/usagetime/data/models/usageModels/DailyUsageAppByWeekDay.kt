package ru.andrewvhub.usagetime.data.models.usageModels

import ru.andrewvhub.utils.extension.getIndexDayOfWeekNumberFromTimestamp

data class DailyUsageAppByWeekDay(
    val indexDayOfWeek: Int,
    val dateMs: Long,
    val totalUsageTime: Long,
    val appUsageDetails: List<DailyUsageApp>
)

fun DailyUsage.toModel() = DailyUsageAppByWeekDay(
    indexDayOfWeek = dateMs.getIndexDayOfWeekNumberFromTimestamp(),
    dateMs = dateMs,
    totalUsageTime = totalTimeMs,
    appUsageDetails = appUsageDetails
)