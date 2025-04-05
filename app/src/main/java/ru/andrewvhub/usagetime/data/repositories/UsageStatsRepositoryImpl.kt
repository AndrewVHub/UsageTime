package ru.andrewvhub.usagetime.data.repositories

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager.INTERVAL_DAILY
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.andrewvhub.usagetime.data.models.DataFetchException
import ru.andrewvhub.usagetime.data.models.OperationResult
import ru.andrewvhub.usagetime.data.models.getOrError
import ru.andrewvhub.usagetime.data.models.usageModels.AppUsagePeriodDetail
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsage
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsageApp
import ru.andrewvhub.usagetime.domain.providers.PackageInfoProvider
import ru.andrewvhub.usagetime.domain.providers.UsageStatsProvider
import ru.andrewvhub.usagetime.domain.repositories.UsageStatsRepository
import java.time.Instant
import java.time.ZoneId

class UsageStatsRepositoryImpl(
    private val usageStatsProvider: UsageStatsProvider,
    private val packageInfoProvider: PackageInfoProvider
) : UsageStatsRepository {

    override suspend fun getUsageForPeriod(
        startDate: Long,
        endDate: Long
    ): OperationResult<List<DailyUsage>>  = withContext(Dispatchers.IO) {
        runCatching {
            val daysPeriod = getDayRanges(startDate, endDate)
            val dailyUsageApp = mutableSetOf<DailyUsageApp>()
            val dailyTotalUsage = mutableListOf<DailyUsage>()
            daysPeriod.forEach { (start, end) ->
                val oneDayPackageNames = usageStatsProvider.getPackagesName(INTERVAL_DAILY, start, end)
                oneDayPackageNames.forEach { packageName ->
                    val usageAppByDay = calculateActiveUsageForAppDay(
                        packageName,
                        start,
                        end
                    ).getOrError()
                    if (usageAppByDay.usageMs > 0)
                        dailyUsageApp.add(usageAppByDay)
                }
                var totalTimeMs = 0L
                dailyUsageApp.forEach {
                    totalTimeMs += it.usageMs
                }
                val dailyTotalUsageByApp = DailyUsage(
                    totalTimeMs = totalTimeMs,
                    totalLaunchCount = 0,
                    appUsageDetails = dailyUsageApp.toList()
                )
                dailyTotalUsage.add(dailyTotalUsageByApp)
                dailyUsageApp.clear()
            }
            OperationResult.Success(dailyTotalUsage)
        }.getOrElse { e ->
            Log.e("Repository", "Ошибка при получении статистики за период: ${e.message}")
            OperationResult.Error(DataFetchException("Ошибка при получении статистики за период: ${e.message}"))
        }
    }

    override suspend fun getUsageForAppPeriod(
        packageName: String,
        startDate: Long,
        endDate: Long
    ): OperationResult<AppUsagePeriodDetail> = withContext(Dispatchers.IO)  {
        runCatching {
            val daysPeriod = getDayRanges(startDate, endDate)

            val activeUsageAppsByDay = mutableListOf<DailyUsageApp>()
            daysPeriod.forEach { (start, end) ->
                activeUsageAppsByDay.add(calculateActiveUsageForAppDay(
                    packageName,
                    start,
                    end
                ).getOrError())
            }
            val totalUsageMs = activeUsageAppsByDay.sumOf {
                it.usageMs
            }
            val appName = packageInfoProvider.getNameAppByPackageName(packageName)
            OperationResult.Success(AppUsagePeriodDetail(
                appName = appName,
                packageName = packageName,
                totalUsageMs = totalUsageMs,
                totalLaunchCount = 0,
                dailyUsage = activeUsageAppsByDay,
            ))
        }.getOrElse { e ->
            Log.d("Repository", "Ошибка при получении статистики для приложения за период: ${e.message}")
            OperationResult.Error(DataFetchException("Ошибка при получении статистики для приложения за период: ${e.message}"))
        }
    }

    override suspend fun calculateActiveUsageForAppDay(
        packageName: String,
        startTime: Long,
        endTime: Long
    ): OperationResult<DailyUsageApp> = withContext(Dispatchers.IO){
        runCatching {
            val usageEvents = usageStatsProvider.queryEvents(startTime, endTime)
            var activeTime = 0L
            var foregroundTimestamp = 0L
            val event = UsageEvents.Event()
            val foregroundEventType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                UsageEvents.Event.ACTIVITY_RESUMED
            } else {
                UsageEvents.Event.MOVE_TO_FOREGROUND
            }
            val backgroundEventType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                UsageEvents.Event.ACTIVITY_PAUSED
            } else {
                UsageEvents.Event.MOVE_TO_BACKGROUND
            }

            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.packageName != packageName) continue
                when (event.eventType) {
                    foregroundEventType -> {
                        foregroundTimestamp = event.timeStamp
                    }
                    backgroundEventType -> {
                        if (foregroundTimestamp > 0) {
                            val sessionTime = event.timeStamp - foregroundTimestamp
                            activeTime += sessionTime
                            foregroundTimestamp = 0L
                        }
                    }
                }
            }

            val appName = packageInfoProvider.getNameAppByPackageName(packageName)
            val appIcon = packageInfoProvider.getImageAppByPackageName(packageName)
            OperationResult.Success(DailyUsageApp(
                appName = appName,
                packageName = packageName,
                dateMs = startTime,
                usageMs = activeTime,
                launchCount = 0,
                icon = appIcon
            ))
        }.getOrElse { e ->
            Log.d("Repository", "Ошибка при получении статистики для приложения за период: ${e.message}")
            OperationResult.Error(DataFetchException("Ошибка при получении статистики для приложения за период: ${e.message}"))
        }
    }

    private fun getDayRanges(startDate: Long, endDate: Long): List<Pair<Long, Long>> {
        val startDay = Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).toLocalDate()
        val endDay = Instant.ofEpochMilli(endDate).atZone(ZoneId.systemDefault()).toLocalDate()

        return generateSequence(startDay) { current ->
            current.plusDays(1).takeIf { !it.isAfter(endDay) }
        }.map { date ->
            val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault())
                .minusNanos(1)
                .toInstant()
                .toEpochMilli()
            startOfDay to endOfDay
        }.toList()
    }
}