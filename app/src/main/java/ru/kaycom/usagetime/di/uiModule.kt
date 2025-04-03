package ru.kaycom.usagetime.di

import androidx.recyclerview.widget.DiffUtil
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.kaycom.usagetime.ui.fragments.agreement.AgreementViewModel
import ru.kaycom.usagetime.ui.items.Item
import ru.kaycom.usagetime.ui.items.ItemCallback
import ru.kaycom.utils.adapter.Adapter

val uiModule = module {
    viewModel { AgreementViewModel() }

    factory<DiffUtil.ItemCallback<Item>> { ItemCallback() }
    factory { Adapter(get()) }
}