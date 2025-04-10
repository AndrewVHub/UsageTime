package ru.andrewvhub.usagetime.di

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.andrewvhub.usagetime.ui.fragments.agreement.AgreementViewModel
import ru.andrewvhub.usagetime.ui.fragments.detailUsageStatApp.DetailUsageStatAppViewModel
import ru.andrewvhub.usagetime.ui.fragments.main.MainViewModel
import ru.andrewvhub.usagetime.ui.fragments.splash.SplashViewModel
import ru.andrewvhub.usagetime.ui.items.Item
import ru.andrewvhub.usagetime.ui.items.ItemCallback
import ru.andrewvhub.utils.adapter.Adapter

val uiModule = module {
    viewModel { AgreementViewModel() }
    viewModel { MainViewModel(get(), cacheDir = get<Context>().cacheDir) }
    viewModel { (packageName: String) -> DetailUsageStatAppViewModel(packageName, get()) }
    viewModel { SplashViewModel() }

    factory<DiffUtil.ItemCallback<Item>> { ItemCallback() }
    factory { Adapter(get()) }
}