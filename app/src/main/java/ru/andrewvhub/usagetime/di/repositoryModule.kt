package ru.andrewvhub.usagetime.di

import org.koin.dsl.module
import ru.andrewvhub.usagetime.data.repositories.UsageStatsRepositoryImpl
import ru.andrewvhub.usagetime.domain.repositories.UsageStatsRepository

val repositoryModule = module {
    single<UsageStatsRepository> { UsageStatsRepositoryImpl(get(), get()) }
}