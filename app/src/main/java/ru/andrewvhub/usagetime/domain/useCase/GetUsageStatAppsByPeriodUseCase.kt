package ru.andrewvhub.usagetime.domain.useCase

import ru.andrewvhub.usagetime.core.UseCase
import ru.andrewvhub.usagetime.data.models.getOrError
import ru.andrewvhub.usagetime.data.models.usageModels.DailyUsage
import ru.andrewvhub.usagetime.domain.repositories.UsageStatsRepository

class GetUsageStatAppsByPeriodUseCase(
    private val repository: UsageStatsRepository
): UseCase<GetUsageStatAppsByPeriodUseCase.Param, List<DailyUsage>>() {

    override suspend fun execute(params: Param) = repository.getUsageForPeriod(params.startDate, params.endDate).getOrError()

    data class Param(
        val startDate: Long,
        val endDate: Long
    )
}