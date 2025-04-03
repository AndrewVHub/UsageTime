package ru.kaycom.utils.adapter

import ru.kaycom.usagetime.ui.items.Item

interface ItemAdapter {
    fun getItemByPosition(position: Int): Item?
}