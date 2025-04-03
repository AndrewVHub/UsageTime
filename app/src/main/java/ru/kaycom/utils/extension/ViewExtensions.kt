package ru.kaycom.utils.extension

import android.graphics.Rect
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updatePadding

private const val DEFAULT_DEBOUNCE = 300L

fun View.addSystemTopSpace(isPadding: Boolean, targetView: View = this) {
    doOnApplyWindowInsets(isPadding) { _, windowInsets, spaces ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        if (isPadding) {
            targetView.updatePadding(top = spaces.top + insets.top)
        } else {
            targetView.updateMargins(top = spaces.top + insets.top)
        }

        WindowInsetsCompat.Builder(windowInsets).setInsets(
            WindowInsetsCompat.Type.systemBars(),
            Insets.of(
                insets.left,
                0,
                insets.right,
                insets.bottom
            )
        ).build()
    }
}

fun View.doOnApplyWindowInsets(
    isPadding: Boolean,
    block: (View, WindowInsetsCompat, Rect) -> WindowInsetsCompat
) {
    val initialSpace = recordViewSpaces(isPadding)

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        block(v, windowInsets, initialSpace)
    }

    requestApplyInsetsWhenAttached()
}

private fun View.recordViewSpaces(isPadding: Boolean) = if (isPadding) {
    Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)
} else {
    Rect(marginLeft, marginTop, marginRight, marginBottom)
}

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun View.updateMargins(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) {
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(left, top, right, bottom)
    }
}

fun View.setOnThrottleClickListener(action: () -> Unit) =
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < DEFAULT_DEBOUNCE) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })