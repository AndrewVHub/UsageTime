package ru.kaycom.usagetime.ui.items

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.kaycom.usagetime.core.BaseViewHolder
import ru.kaycom.usagetime.databinding.ItemEmptyBinding
import ru.kaycom.usagetime.ui.viewHolders.SimpleViewHolder

enum class ItemContentType {
    EmptyItemType {
        override fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder<*> =
            SimpleViewHolder<EmptyItem>(
                ItemEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
    };

    abstract fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder<*>
}