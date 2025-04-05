package ru.andrewvhub.usagetime.di

import org.koin.dsl.module
import ru.andrewvhub.usagetime.domain.useCase.UseCaseTest

val useCaseModule = module {
    single { UseCaseTest(get()) }
}