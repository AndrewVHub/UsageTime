package ru.kaycom.usagetime.ui.viewHolders

import androidx.viewbinding.ViewBinding
import ru.kaycom.usagetime.core.BaseViewHolder
import ru.kaycom.usagetime.ui.items.Item

class SimpleViewHolder<T : Item>(viewBinding: ViewBinding) : BaseViewHolder<T>(viewBinding.root) {
    override fun bind(item: T) {}
}