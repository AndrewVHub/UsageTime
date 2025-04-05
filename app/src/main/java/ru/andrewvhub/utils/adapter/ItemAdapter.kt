package ru.andrewvhub.utils.adapter

import ru.andrewvhub.usagetime.ui.items.Item

interface ItemAdapter {
    fun getItemByPosition(position: Int): Item?
}