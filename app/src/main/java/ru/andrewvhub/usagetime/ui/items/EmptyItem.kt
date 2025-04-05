package ru.andrewvhub.usagetime.ui.items

import java.util.UUID

data object EmptyItem : Item {
    override val id: String get() = UUID.randomUUID().toString()

    override fun areItemsSame(newItem: Item): Boolean = false

    override fun getType(): ItemContentType = ItemContentType.EmptyItemType
}