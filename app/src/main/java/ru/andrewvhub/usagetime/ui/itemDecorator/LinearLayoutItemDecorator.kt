package ru.andrewvhub.usagetime.ui.itemDecorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

private const val DEFAULT_OUT_SPACE = 0

class LinearLayoutItemDecorator(
    private val top: Int = DEFAULT_OUT_SPACE,
    private val bottom: Int = DEFAULT_OUT_SPACE,
    private val left: Int = DEFAULT_OUT_SPACE,
    private val right: Int = DEFAULT_OUT_SPACE,
    private val divider: Int = DEFAULT_OUT_SPACE,
    private val orientation: Int = RecyclerView.VERTICAL
) : RecyclerView.ItemDecoration() {

    constructor(
        horizontal: Int = DEFAULT_OUT_SPACE,
        vertical: Int = DEFAULT_OUT_SPACE,
        divider: Int = DEFAULT_OUT_SPACE,
        orientation: Int = RecyclerView.VERTICAL
    ) : this(vertical, vertical, horizontal, horizontal, divider, orientation)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val lastItemPosition = (parent.adapter?.itemCount ?: 0) - 1
        if (orientation == RecyclerView.VERTICAL) {
            outRect.left = left
            outRect.right = right
            outRect.top = if (position == 0) top else divider
            if (position == lastItemPosition) outRect.bottom = bottom
        } else {
            outRect.top = top
            outRect.bottom = bottom
            outRect.left = if (position == 0) left else divider
            if (position == lastItemPosition) outRect.right = right
        }
    }
}