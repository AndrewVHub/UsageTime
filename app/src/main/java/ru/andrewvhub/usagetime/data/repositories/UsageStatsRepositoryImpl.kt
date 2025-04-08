package ru.andrewvhub.usagetime.data.repositories

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager.INTERVAL_DAILY
import android.os.Build
import ru.andrewvhub.usagetime.data.models.OperationResult
import ru.andrewvhub.usagetime.data.models.UsageErrorException
import ru.andrewvhub.usagetime.data.models.getOrError
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

    /**
     * Получает статистику использования приложений за определенный период.
     *
     * @param startDate Начало периода в миллисекундах.
     * @param endDate Конец периода в миллисекундах.
     * @return `OperationResult.Success` с списком объектов `DailyUsage`, содержащих статистику использования приложений за каждый день периода,
     * или `OperationResult.Error` в случае ошибки.
     * @throws UsageErrorException.UsageForPeriodException если произошла ошибка при получении статистики.
     */
    override suspend fun getUsageForPeriod(
        startDate: Long,
        endDate: Long
    ): OperationResult<List<DailyUsage>> {
        runCatching {
            val daysPeriod = getDayRanges(startDate, endDate)
            val dailyUsageApp = mutableSetOf<DailyUsageApp>()
            val dailyTotalUsage = mutableListOf<DailyUsage>()
            daysPeriod.forEach { (start, end) ->
                val oneDayPackageNames =
                    usageStatsProvider.getPackagesName(INTERVAL_DAILY, start, end)
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
                    dateMs = start,
                    totalTimeMs = totalTimeMs,
                    appUsageDetails = dailyUsageApp.toList()
                )
                dailyTotalUsage.add(dailyTotalUsageByApp)
                dailyUsageApp.clear()
            }
            return OperationResult.Success(dailyTotalUsage)
        }.getOrElse {
            return OperationResult.Error(UsageErrorException.UsageForPeriodException())
        }
    }

    /**
     * Получает статистику использования приложения за определенный период.
     *
     * @param packageName Имя пакета приложения.
     * @param startDate Начало периода в миллисекундах.
     * @param endDate Конец периода в миллисекундах.
     * @return `OperationResult.Success` с списком объектов `DailyUsageApp`, содержащих статистику использования приложения за каждый день периода,
     * или `OperationResult.Error` в случае ошибки.
     * @throws UsageErrorException.UsageForAppPeriodException если произошла ошибка при получении статистики.
     *
     */
    override suspend fun getUsageForAppPeriod(
        packageName: String,
        startDate: Long,
        endDate: Long
    ): OperationResult<List<DailyUsageApp>> {
        runCatching {
            val daysPeriod = getDayRanges(startDate, endDate)
            val activeUsageAppsByDay = mutableListOf<DailyUsageApp>()
            daysPeriod.forEach { (start, end) ->
                activeUsageAppsByDay.add(
                    calculateActiveUsageForAppDay(
                        packageName,
                        start,
                        end
                    ).getOrError()
                )
            }
            return OperationResult.Success(activeUsageAppsByDay)

        }.getOrElse {
            return OperationResult.Error(UsageErrorException.UsageForAppPeriodException())
        }
    }

    /**
     * Вычисляет активное время использования приложения за один день.
     *
     * @param packageName Имя пакета приложения.
     * @param startTime Начало периода в миллисекундах.
     * @param endTime Конец периода в миллисекундах.
     * @return `OperationResult.Success` с объектом `DailyUsageApp`, содержащим статистику использования приложения за день, или `OperationResult.Error` в случае ошибки.
     * @throws UsageErrorException.UsageForAppPeriodException если произошла ошибка при получении статистики.
     */
    override suspend fun calculateActiveUsageForAppDay(
        packageName: String,
        startTime: Long,
        endTime: Long
    ): OperationResult<DailyUsageApp> {
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
            return OperationResult.Success(
                DailyUsageApp(
                    appName = appName,
                    packageName = packageName,
                    dateMs = startTime,
                    usageMs = activeTime,
                    icon = appIcon
                )
            )
        }.getOrElse {
            return OperationResult.Error(UsageErrorException.ActiveUsageForAppDayException())
        }
    }

    /**
     * Получает список пар (начало дня, конец дня) в миллисекундах для заданного периода.
     *
     * @param startDate Начало периода в миллисекундах.
     * @param endDate Конец периода в миллисекундах.
     * @return Список пар, где каждая пара представляет собой начало и конец дня в миллисекундах.
     *         Например: [(1678886400000, 1678972799999), (1678972800000, 1679059199999)]
     *         Они представляют собой временной диапазон для каждого дня в заданном интервале.
     */
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