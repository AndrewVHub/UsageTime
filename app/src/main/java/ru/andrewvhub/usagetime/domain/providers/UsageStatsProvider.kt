package ru.andrewvhub.usagetime.domain.providers

import android.app.usage.UsageEvents
import android.app.usage.UsageStats

interface UsageStatsProvider {
    fun queryUsageStats(intervalType: Int, beginTime: Long, endTime: Long): List<UsageStats>
    fun getPackagesName(intervalType: Int, beginTime: Long, endTime: Long): List<String>
    fun queryEvents(beginTime: Long, endTime: Long): UsageEvents
}