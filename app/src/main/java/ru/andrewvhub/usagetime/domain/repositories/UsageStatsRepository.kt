package ru.andrewvhub.usagetime.domain.repositories

import ru.andrewvhub.usagetime.data.models.OperationResult
import ru.andrewvhub.usagetime.data.models.usageModels.AppUsagePeriodDetail
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsage
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsageApp

interface UsageStatsRepository {
    suspend fun getUsageForPeriod(startDate: Long, endDate: Long): OperationResult<List<DailyUsage>>
    suspend fun getUsageForAppPeriod(packageName: String,startDate: Long, endDate: Long): OperationResult<AppUsagePeriodDetail>
    suspend fun calculateActiveUsageForAppDay(packageName: String, startTime: Long, endTime: Long): OperationResult<DailyUsageApp>
}