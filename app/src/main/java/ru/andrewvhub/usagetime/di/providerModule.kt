package ru.andrewvhub.usagetime.di

import org.koin.dsl.module
import ru.andrewvhub.usagetime.data.providers.PackageInfoProviderImpl
import ru.andrewvhub.usagetime.data.providers.UsageStatsProviderImpl
import ru.andrewvhub.usagetime.domain.providers.PackageInfoProvider
import ru.andrewvhub.usagetime.domain.providers.UsageStatsProvider

val providerModule = module {
    single<UsageStatsProvider> { UsageStatsProviderImpl(get()) }
    single<PackageInfoProvider> { PackageInfoProviderImpl(get()) }
}