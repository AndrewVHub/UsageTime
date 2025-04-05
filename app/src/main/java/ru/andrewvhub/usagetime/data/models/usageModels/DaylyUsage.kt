package ru.andrewvhub.usagetime.data.models.usageModels

// Модель данных для одного дня (общая статистика)
data class DailyUsage(
    val totalTimeMs: Long,
    val totalLaunchCount: Int,             // Общее количество запусков всех приложений за этот день
    val appUsageDetails: List<DailyUsageApp>
)