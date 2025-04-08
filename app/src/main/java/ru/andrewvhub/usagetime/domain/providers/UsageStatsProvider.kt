package ru.andrewvhub.usagetime.domain.providers

import android.app.usage.UsageEvents

interface UsageStatsProvider {
    fun getPackagesName(intervalType: Int, beginTime: Long, endTime: Long): List<String>
    fun queryEvents(beginTime: Long, endTime: Long): UsageEvents
}