package ru.andrewvhub.usagetime.data.models.usageModels

data class DailyUsage(
    val dateMs: Long,
    val totalTimeMs: Long,
    val appUsageDetails: List<DailyUsageApp>
)