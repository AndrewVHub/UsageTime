package ru.andrewvhub.usagetime.ui.fragments.detailUsageStatApp

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.core.BaseFragment
import ru.andrewvhub.usagetime.databinding.FragmentDetailUsageStatAppBinding
import ru.andrewvhub.usagetime.ui.viewBinding.viewBinding
import ru.andrewvhub.utils.extension.addSystemTopSpace
import ru.andrewvhub.utils.extension.formatDate
import ru.andrewvhub.utils.extension.getColor
import ru.andrewvhub.utils.extension.getFont
import ru.andrewvhub.utils.extension.load
import ru.andrewvhub.utils.extension.nonNullObserve
import ru.andrewvhub.utils.extension.setOnThrottleClickListener
import ru.andrewvhub.utils.extension.showSnackBar

class DetailUsageStatAppFragment : BaseFragment(R.layout.fragment_detail_usage_stat_app) {

    private val args: DetailUsageStatAppFragmentArgs by navArgs()
    private val viewBinding by viewBinding(FragmentDetailUsageStatAppBinding::bind)
    override val viewModel by viewModel<DetailUsageStatAppViewModel> { parametersOf(args.packageName) }

    private var currentHighlight: Highlight? = null

    private val onChartValueSelectedListener: OnChartValueSelectedListener
        get() = object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is BarEntry && currentHighlight?.x != h?.x) {
                    currentHighlight = h
                    val dayOfWeekIndex = e.x.toInt()
                    viewModel.getOneDayUsageByDay(dayOfWeekIndex)
                }
            }

            override fun onNothingSelected() {
                currentHighlight?.let {
                    viewBinding.chart.highlightValue(it, false)
                }
            }
        }

    private val valueFormatterYAxis: ValueFormatter
        get() = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return getString(
                    R.string.main_fragment_chart_item_counter_history_hours_unit,
                    value
                )
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(viewBinding) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.addSystemTopSpace(false)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        previousWeek.setOnThrottleClickListener {
            viewModel.getPreviousWeekRange()
        }
        nextWeek.setOnThrottleClickListener {
            viewModel.getNextWeekRange()
        }

        refresh.setOnThrottleClickListener {
            viewModel.refreshData()
        }

        viewModel.apply {
            dailyUsageBarEntryList.nonNullObserve(viewLifecycleOwner, ::handleChartHistory)
            highlightLiveData.nonNullObserve(viewLifecycleOwner) {
                currentHighlight = it
                highlightLastBar(it)
            }
            totalTimeByDayLiveData.nonNullObserve(viewLifecycleOwner) {
                totalUsageTime.text = it
            }
            currentDateMsLiveData.nonNullObserve(viewLifecycleOwner) {
                currentDay.text = it.formatDate()
            }

            appImageLiveData.nonNullObserve(viewLifecycleOwner) { appIcon.load(it) }
            errorMessage.nonNullObserve(viewLifecycleOwner) { showSnackBar(it) }
            isLoadingLiveData.nonNullObserve(viewLifecycleOwner) {
                refresh.isEnabled = !it
            }
        }
    }

    private fun handleChartHistory(items: List<BarEntry>) {
        val barDataUsageHours = BarDataSet(items.toList(), "usage_hours")
        barDataUsageHours.color = getColor(R.color.main_color_active)
        barDataUsageHours.highLightColor = getColor(R.color.main_color_gray_bb)
        BarData(barDataUsageHours).apply {
            barWidth = 0.23f
            isHighlightEnabled = true
            setDrawValues(false)
            viewBinding.chart.data = this
        }
        processXAxis()
        processYAxis()
        processChartSettings()
    }

    // Настройка x-оси графика
    private fun processXAxis() {
        val font = getFont(R.font.mulish_500)
        val days = resources.getStringArray(R.array.days)
        viewBinding.chart.xAxis.apply {
            setCenterAxisLabels(false)
            gridColor = getColor(R.color.background_secondary)
            axisLineColor = getColor(R.color.background_secondary)
            valueFormatter = IndexAxisValueFormatter(days)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            isGranularityEnabled = true
            typeface = font
            textColor = getColor(R.color.background_secondary)
            textSize = 10f
        }
    }

    // Настройка y-оси графика
    private fun processYAxis() {
        val font = getFont(R.font.mulish_500)
        viewBinding.chart.axisRight.isEnabled = false
        viewBinding.chart.axisLeft.apply {
            gridColor = getColor(R.color.white)
            gridLineWidth = 1.5f
            setDrawZeroLine(false)
            setDrawAxisLine(false)
            typeface = font
            textColor = getColor(R.color.background_secondary)
            textSize = 10f
            valueFormatter = valueFormatterYAxis
        }
    }

    private fun processChartSettings(): Unit = with(viewBinding) {
        chart.apply {
            setDrawValueAboveBar(false)
            xAxis.axisMinimum = data.xMin - 0.5f
            xAxis.axisMaximum = data.xMax + 0.5f
            isDragEnabled = true
            isDoubleTapToZoomEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setVisibleXRangeMaximum(7f)
            animateXY(1000, 1000)
            setOnChartValueSelectedListener(onChartValueSelectedListener)
            invalidate()
            currentHighlight?.let { highlightLastBar(it) }
        }
        toolbar.title = getString(R.string.main_fragment_title_done)
    }

    private fun highlightLastBar(currentHighlight: Highlight) {
        viewBinding.chart.apply {
            highlightValue(currentHighlight, false)
        }
    }
}