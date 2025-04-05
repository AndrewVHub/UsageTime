package ru.andrewvhub.usagetime.data.providers

import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.os.Build
import ru.andrewvhub.usagetime.domain.providers.UsageStatsProvider

class UsageStatsProviderImpl(
    private val usageStatsManager: UsageStatsManager
) : UsageStatsProvider {
    override fun queryUsageStats(
        intervalType: Int,
        beginTime: Long,
        endTime: Long
    ): List<UsageStats> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val aggregatedStats = usageStatsManager.queryAndAggregateUsageStats(beginTime, endTime)
            aggregatedStats.values.toList()
        } else {
            usageStatsManager.queryUsageStats(intervalType, beginTime, endTime).orEmpty()
        }
    }

    override fun getPackagesName(intervalType: Int, beginTime: Long, endTime: Long): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val aggregatedStats = usageStatsManager.queryAndAggregateUsageStats(beginTime, endTime)
            aggregatedStats.keys.toList()
        } else {
            usageStatsManager.queryUsageStats(intervalType, beginTime, endTime).orEmpty().map { it.packageName }
        }
    }

    override fun queryEvents(beginTime: Long, endTime: Long): UsageEvents =
        usageStatsManager.queryEvents(beginTime, endTime)
}