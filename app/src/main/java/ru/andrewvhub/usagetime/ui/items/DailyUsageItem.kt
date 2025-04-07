package ru.andrewvhub.usagetime.ui.items

import android.graphics.drawable.Drawable

data class DailyUsageItem(
    val appIcon: Drawable?,
    val appName: String,
    val appUsageTime: String,
    val onClick: () -> Unit
): Item {
    override val id: String
        get() = "${appName}_${appUsageTime}"

    override fun getType(): ItemContentType = ItemContentType.DailyUsageItemType

    override fun areContentsSame(newItem: Item): Boolean {
        newItem as DailyUsageItem
        return appIcon == newItem.appIcon
                && id == newItem.id
                && appName == newItem.appName
                && appUsageTime == newItem.appUsageTime
    }

    override fun getChangePayload(newItem: Item): Any {
        newItem as DailyUsageItem
        val diff = mutableSetOf<String>()
        if (appIcon != newItem.appIcon) diff.add(ICON_KEY)
        if (appName != newItem.appName) diff.add(NAME_KEY)
        if (appUsageTime != newItem.appUsageTime) diff.add(USAGE_TIME_KEY)
        return diff
    }

    companion object {
        const val ICON_KEY = "ICON_KEY"
        const val NAME_KEY = "NAME_KEY"
        const val USAGE_TIME_KEY = "USAGE_TIME_KEY"
    }
}