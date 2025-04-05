package ru.andrewvhub.usagetime.data.models.usageModels

data class AppUsagePeriodDetail(
    val appName: String,
    val packageName: String,
    val totalUsageMs: Long,
    val totalLaunchCount: Int,
    val dailyUsage: List<DailyUsageApp>
)