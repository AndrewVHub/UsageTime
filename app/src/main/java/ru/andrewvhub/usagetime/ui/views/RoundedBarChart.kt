package ru.andrewvhub.usagetime.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import ru.andrewvhub.usagetime.R


/**
 * Код: https://gist.github.com/xanscale/e971cc4f2f0712a8a3bcc35e85325c27
 **/
class RoundedBarChart : BarChart {
    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        readRadiusAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        readRadiusAttr(context, attrs)
    }

    private fun readRadiusAttr(context: Context, attrs: AttributeSet) {
        val radius =
            context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedBarChart, 0, 0)
        try {
            setRadius(radius.getDimensionPixelSize(R.styleable.RoundedBarChart_radius, 0))
        } finally {
            radius.recycle()
        }
    }

    private fun setRadius(radius: Int) {
        renderer = RoundedBarChartRenderer(this, animator, viewPortHandler, radius)
    }

    private inner class RoundedBarChartRenderer(
        chart: BarDataProvider?,
        animator: ChartAnimator?,
        viewPortHandler: ViewPortHandler?,
        private val mRadius: Int
    ) : BarChartRenderer(chart, animator, viewPortHandler) {
        override fun drawHighlighted(c: Canvas, indices: Array<Highlight?>) {
            val barData = mChart.barData
            for (high in indices) {
                val set = high?.let { barData.getDataSetByIndex(it.dataSetIndex) }
                if (set == null || !set.isHighlightEnabled) continue
                val e = set.getEntryForXValue(high.x, high.y)
                if (!isInBoundsX(e, set)) continue
                val trans = mChart.getTransformer(set.axisDependency)
                mHighlightPaint.color = set.highLightColor
                mHighlightPaint.alpha = set.highLightAlpha
                val isStack = high.stackIndex >= 0 && e.isStacked
                val y1: Float
                val y2: Float
                if (isStack) {
                    if (mChart.isHighlightFullBarEnabled) {
                        y1 = e.positiveSum
                        y2 = -e.negativeSum
                    } else {
                        val range = e.ranges[high.stackIndex]
                        y1 = range.from
                        y2 = range.to
                    }
                } else {
                    y1 = e.y
                    y2 = 0f
                }
                prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans)
                setHighlightDrawPos(high, mBarRect)
                c.drawRoundRect(mBarRect, 0f, mRadius.toFloat(), mHighlightPaint)
            }
        }

        override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
            val trans = mChart.getTransformer(dataSet.axisDependency)
            mShadowPaint.color = dataSet.barShadowColor
            val phaseX = mAnimator.phaseX
            val phaseY = mAnimator.phaseY

            // initialize the buffer
            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setBarWidth(mChart.barData.barWidth)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.feed(dataSet)
            trans.pointValuesToPixel(buffer.buffer)

            // if multiple colors
            if (dataSet.colors.size > 1) {
                var j = 0
                while (j < buffer.size()) {
                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                        j += 4
                        continue
                    }
                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
                    if (mChart.isDrawBarShadowEnabled) {
                        if (mRadius > 0) c.drawRoundRect(
                            RectF(
                                buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()
                            ),
                            mRadius.toFloat(), mRadius.toFloat(), mShadowPaint
                        ) else c.drawRect(
                            buffer.buffer[j], mViewPortHandler.contentTop(),
                            buffer.buffer[j + 2],
                            mViewPortHandler.contentBottom(), mShadowPaint
                        )
                    }

                    // Set the color for the currently drawn value. If the index
                    // is
                    // out of bounds, reuse colors.
                    mRenderPaint.color = dataSet.getColor(j / 4)
                    if (mRadius > 0) c.drawRoundRect(
                        RectF(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]
                        ),
                        mRadius.toFloat(), mRadius.toFloat(), mRenderPaint
                    ) else c.drawRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint
                    )
                    j += 4
                }
            } else {
                mRenderPaint.color = dataSet.color
                var j = 0
                while (j < buffer.size()) {
                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                        j += 4
                        continue
                    }
                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
                    if (mChart.isDrawBarShadowEnabled) {
                        if (mRadius > 0) c.drawRoundRect(
                            RectF(
                                buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()
                            ),
                            mRadius.toFloat(), mRadius.toFloat(), mShadowPaint
                        ) else c.drawRect(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint
                        )
                    }
                    if (mRadius > 0) c.drawRoundRect(
                        RectF(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]
                        ),
                        mRadius.toFloat(), mRadius.toFloat(), mRenderPaint
                    ) else c.drawRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint
                    )
                    j += 4
                }
            }
        }
    }
}