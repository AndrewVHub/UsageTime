package ru.andrewvhub.usagetime

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import ru.andrewvhub.usagetime.di.appModule
import ru.andrewvhub.usagetime.di.providerModule
import ru.andrewvhub.usagetime.di.repositoryModule
import ru.andrewvhub.usagetime.di.uiModule
import ru.andrewvhub.usagetime.di.useCaseModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            fragmentFactory()
            modules(
                uiModule,
                appModule,
                repositoryModule,
                providerModule,
                useCaseModule
            )
        }
    }
}