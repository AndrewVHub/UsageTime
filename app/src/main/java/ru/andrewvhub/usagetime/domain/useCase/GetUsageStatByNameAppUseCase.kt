package ru.andrewvhub.usagetime.domain.useCase

import ru.andrewvhub.usagetime.core.UseCase
import ru.andrewvhub.usagetime.data.models.getOrError
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsageApp
import ru.andrewvhub.usagetime.domain.repositories.UsageStatsRepository

class GetUsageStatByNameAppUseCase(
    private val repository: UsageStatsRepository
) : UseCase<GetUsageStatByNameAppUseCase.Param, List<DailyUsageApp>>() {

    override suspend fun execute(params: Param) =
        repository.getUsageForAppPeriod(params.packageName, params.startDate, params.endDate)
            .getOrError()

    data class Param(
        val packageName: String,
        val startDate: Long,
        val endDate: Long
    )
}