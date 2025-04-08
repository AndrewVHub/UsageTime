package ru.andrewvhub.usagetime.di

import org.koin.dsl.module
import ru.andrewvhub.usagetime.domain.useCase.GetUsageStatAppsByPeriodUseCase
import ru.andrewvhub.usagetime.domain.useCase.GetUsageStatByNameAppUseCase

val useCaseModule = module {
    single { GetUsageStatAppsByPeriodUseCase(get()) }
    single { GetUsageStatByNameAppUseCase(get()) }
}