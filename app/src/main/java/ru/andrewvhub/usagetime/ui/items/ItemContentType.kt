package ru.andrewvhub.usagetime.ui.items

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.andrewvhub.usagetime.core.BaseViewHolder
import ru.andrewvhub.usagetime.databinding.ItemDailyUsageAppBinding
import ru.andrewvhub.usagetime.databinding.ItemEmptyBinding
import ru.andrewvhub.usagetime.ui.viewHolders.DailyUsageAppViewHolder
import ru.andrewvhub.usagetime.ui.viewHolders.SimpleViewHolder

enum class ItemContentType {
    EmptyItemType {
        override fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder<*> =
            SimpleViewHolder<EmptyItem>(
                ItemEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
    },
    DailyUsageItemType {
        override fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder<*> =
            DailyUsageAppViewHolder(
                ItemDailyUsageAppBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )
    };

    abstract fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder<*>
}