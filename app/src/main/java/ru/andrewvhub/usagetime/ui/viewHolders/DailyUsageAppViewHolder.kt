package ru.andrewvhub.usagetime.ui.viewHolders

import ru.andrewvhub.usagetime.core.BaseViewHolder
import ru.andrewvhub.usagetime.databinding.ItemDailyUsageAppBinding
import ru.andrewvhub.usagetime.ui.items.DailyUsageItem
import ru.andrewvhub.utils.extension.load
import ru.andrewvhub.utils.extension.setOnThrottleClickListener

class DailyUsageAppViewHolder(private val viewBinding: ItemDailyUsageAppBinding) :
    BaseViewHolder<DailyUsageItem>(viewBinding.root) {

    init {
        viewBinding.root.setOnThrottleClickListener { getItem()?.onClick?.invoke() }
    }

    override fun bind(item: DailyUsageItem) {
        updateAppName(item)
        updateAppIcon(item)
        updateAppUsageTime(item)
    }

    override fun update(item: DailyUsageItem, payloads: Set<*>) = payloads.forEach {
        when (it) {
            DailyUsageItem.NAME_KEY -> updateAppName(item)
            DailyUsageItem.ICON_KEY -> updateAppIcon(item)
            DailyUsageItem.USAGE_TIME_KEY -> updateAppUsageTime(item)
        }
    }

    private fun updateAppName(item: DailyUsageItem) = with(viewBinding) {
        appName.text = item.appName
    }

    private fun updateAppIcon(item: DailyUsageItem) = with(viewBinding) {
        item.appIcon?.let { appIcon.load(it) }
    }

    private fun updateAppUsageTime(item: DailyUsageItem) = with(viewBinding) {
        appUsageTime.text = item.appUsageTime
    }
}