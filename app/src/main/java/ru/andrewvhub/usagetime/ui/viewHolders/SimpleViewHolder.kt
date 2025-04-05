package ru.andrewvhub.usagetime.ui.viewHolders

import androidx.viewbinding.ViewBinding
import ru.andrewvhub.usagetime.core.BaseViewHolder
import ru.andrewvhub.usagetime.ui.items.Item

class SimpleViewHolder<T : Item>(viewBinding: ViewBinding) : BaseViewHolder<T>(viewBinding.root) {
    override fun bind(item: T) {}
}