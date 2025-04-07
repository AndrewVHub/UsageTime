package ru.andrewvhub.usagetime.di

import org.koin.dsl.module
import ru.andrewvhub.usagetime.domain.useCase.GetUsageStatAppByPeriodUseCase

val useCaseModule = module {
    single { GetUsageStatAppByPeriodUseCase(get()) }
}