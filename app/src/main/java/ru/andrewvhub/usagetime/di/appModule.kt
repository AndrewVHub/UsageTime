package ru.andrewvhub.usagetime.di

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single<Resources> { androidApplication().applicationContext.resources }
    single<UsageStatsManager> {
        get<Context>().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }
    single<PackageManager> { get<Context>().packageManager }
}