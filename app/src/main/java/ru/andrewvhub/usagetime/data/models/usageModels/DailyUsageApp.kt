package ru.andrewvhub.usagetime.data.models.usageModels

import android.graphics.drawable.Drawable

data class DailyUsageApp(
    val appName: String,
    val packageName: String,
    val dateMs: Long,
    val usageMs: Long,
    val launchCount: Int,
    val icon: Drawable? = null
)