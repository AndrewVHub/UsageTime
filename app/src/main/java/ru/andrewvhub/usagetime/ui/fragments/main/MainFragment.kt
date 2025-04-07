package ru.andrewvhub.usagetime.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.core.BaseFragment
import ru.andrewvhub.usagetime.databinding.FragmentMainBinding
import ru.andrewvhub.usagetime.ui.itemDecorator.LinearLayoutItemDecorator
import ru.andrewvhub.usagetime.ui.viewBinding.viewBinding
import ru.andrewvhub.utils.adapter.Adapter
import ru.andrewvhub.utils.extension.addSystemBottomSpace
import ru.andrewvhub.utils.extension.addSystemTopSpace
import ru.andrewvhub.utils.extension.dp
import ru.andrewvhub.utils.extension.formatDate
import ru.andrewvhub.utils.extension.getColor
import ru.andrewvhub.utils.extension.nonNullObserve
import ru.andrewvhub.utils.extension.setOnThrottleClickListener

class MainFragment : BaseFragment(R.layout.fragment_main) {

    private val viewBinding by viewBinding(FragmentMainBinding::bind)
    override val viewModel by viewModel<MainViewModel>()
    private val adapter: Adapter by inject()

    private var currentHighlight: Highlight? = null

    private val onChartValueSelectedListener: OnChartValueSelectedListener
        get() = object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is BarEntry && currentHighlight?.x != h?.x) {
                    currentHighlight = h
                    val dayOfWeekIndex = e.x.toInt() // Получаем индекс дня недели (0-6)
                    viewModel.getOneDayUsageByDay(dayOfWeekIndex)
                    val usageHours = e.y // Получаем значение usageHours
                    val days = resources.getStringArray(R.array.days)
                    val dayOfWeek = days[dayOfWeekIndex]

                    Log.d("OS4:ChartInteraction", "Выбран день: $dayOfWeek ($dayOfWeekIndex), Использование: $usageHours часов")

                }
            }

            override fun onNothingSelected() {
                Log.d("OS4:ChartInteraction", "Ничего не выбрано")
                currentHighlight?.let {
                    viewBinding.chart.highlightValue(it, false)
                    Log.d("OS4:ChartInteraction", "Получилось оставить Highlight")
                }
            }
        }

    private val valueFormatterYAxis: ValueFormatter
        get() = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return getString(R.string.main_fragment_chart_item_counter_history_hours_unit, value)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(viewBinding) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.addSystemTopSpace(false)
        recyclerView.addSystemBottomSpace(true)

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            LinearLayoutItemDecorator(
                left = resources.getDimensionPixelSize(R.dimen.space_common_side),
                right = resources.getDimensionPixelSize(R.dimen.space_common_side),
                divider = 24.dp
            )
        )

        previousWeek.setOnThrottleClickListener {
            viewModel.getPreviousWeekRange()
        }
        nextWeek.setOnThrottleClickListener {
            viewModel.getNextWeekRange()
        }

        viewModel.apply {
            dailyUsageList.nonNullObserve(viewLifecycleOwner,::handleChartHistory)
            highlightLiveData.nonNullObserve(viewLifecycleOwner) {
                currentHighlight = it
            }

            dailyUsageForPeriodLiveData.nonNullObserve(viewLifecycleOwner) {
                adapter.setCollection(it) {
                    recyclerView.smoothScrollToPosition(0)
                }
            }

            totalTimeByDayLiveData.nonNullObserve(viewLifecycleOwner) {
                totalUsageTime.text = it
            }
            currentDateMsLiveData.nonNullObserve(viewLifecycleOwner) {
                currentDay.text = it.formatDate()
            }

            isLoading.nonNullObserve(viewLifecycleOwner,::handleLoading)
        }
    }

    private fun handleLoading(isLoading: Boolean): Unit = with(viewBinding) {
        if (isLoading) {
            loader.resumeAnimation()
            toolbar.title = getString(R.string.main_fragment_title_loading)
            contentGroup.isVisible = false
            loader.isVisible = true
        } else {
            toolbar.title = getString(R.string.main_fragment_title_done)
            loader.pauseAnimation()
            contentGroup.isVisible = true
            loader.isVisible = false
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
        val font = ResourcesCompat.getFont(requireContext(), R.font.mulish_500)
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
            textColor = getColor(R.color.fill_tertiary)
            textSize = 10f
        }
    }

    // Настройка y-оси графика
    private fun processYAxis() {
        val font = ResourcesCompat.getFont(requireContext(), R.font.mulish_500)
        viewBinding.chart.axisRight.isEnabled = false
        viewBinding.chart.axisLeft.apply {
            gridColor = getColor(R.color.white)
            gridLineWidth = 1.5f
            setDrawZeroLine(false)
            setDrawAxisLine(false)
            typeface = font
            textColor = getColor(R.color.fill_secondary)
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