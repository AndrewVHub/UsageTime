package ru.kaycom.utils.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.kaycom.usagetime.core.BaseViewHolder
import ru.kaycom.usagetime.ui.items.Item
import ru.kaycom.usagetime.ui.items.ItemContentType

class Adapter(
    diffCallback: DiffUtil.ItemCallback<Item>
) : RecyclerView.Adapter<BaseViewHolder<*>>(), ItemAdapter {

    fun setCollection(list: List<Item>, commitCallback: Runnable? = null) =
        differ.submitList(list, commitCallback)

    fun getCurrentCollection() = differ.currentList.toList()

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<*> {
        val itemType = ItemContentType.entries.find { it.ordinal == viewType }
            ?: ItemContentType.EmptyItemType
        return itemType.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) =
        onBindHolder(holder, position)

    override fun onBindViewHolder(
        holder: BaseViewHolder<*>,
        position: Int,
        payloads: MutableList<Any>
    ) = onBindHolder(holder, position, payloads)

    @Suppress("UNCHECKED_CAST")
    private fun onBindHolder(
        holder: BaseViewHolder<*>,
        position: Int,
        payloads: MutableList<Any>? = null
    ) {
        holder as BaseViewHolder<Item>
        differ.currentList[position].let {
            if (payloads.isNullOrEmpty()) holder.bind(it) else holder.update(
                it,
                payloads[0] as Set<*>
            )
        }
    }

    override fun getItemViewType(position: Int) = differ.currentList[position].getType().ordinal

    override fun getItemCount(): Int = differ.currentList.size
    override fun getItemByPosition(position: Int): Item? = differ.currentList.getOrNull(position)
}