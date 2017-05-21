package com.grosner.androiddatabaselibrarycomparison2.tests

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TabLayout
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewManager
import android.widget.ProgressBar
import com.andrewgrosner.kbinding.bindings.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.barChart(init: BarChart.() -> Unit): BarChart {
    return ankoView({ BarChart(it) }, theme = 0, init = init)
}

inline fun <T : View> T.coordinatorLParams(width: Int = WRAP_CONTENT, height: Int = WRAP_CONTENT,
                                           behavior: CoordinatorLayout.Behavior<*>): T {
    val layoutParams = CoordinatorLayout.LayoutParams(width, height)
    layoutParams.behavior = behavior
    this.layoutParams = layoutParams
    return this
}


/**
 * Binds the output of the initial expression to [ProgressBar.setProgress] method.
 */
infix fun <Data, Input, TBinding : BindingConverter<Data, Input>>
        OneWayExpression<Data, Input, Int, TBinding>.toTabLayout(tabLayout: TabLayout)
        = toView(tabLayout) { tabLayout, index ->
    if (tabLayout.selectedTabPosition != index) {
        tabLayout.getTabAt(index ?: 0)?.select()
    }
}

fun <Data> TwoWayBindingExpression<Data, Int, Int, ObservableBindingConverter<Data, Int>, TabLayout>.toFieldFromTabLayout(
        inverseSetter: (Data?, Int?) -> Unit = { _, input ->
            oneWayBinding.oneWayExpression.converter.observableField?.let { observableField ->
                observableField.value = input ?: observableField.defaultValue
            }
        })
        = toInput(TabLayoutViewRegister(), inverseSetter)

class TabLayoutViewRegister : ViewRegister<TabLayout, Int>() {
    override fun registerView(view: TabLayout) {
        view.addOnTabSelectedListener(onSelectedListener)
    }

    override fun getValue(view: TabLayout) = view.selectedTabPosition

    override fun deregisterFromView(view: TabLayout) {
        view.removeOnTabSelectedListener(onSelectedListener)
    }

    private val onSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) = Unit

        override fun onTabUnselected(tab: TabLayout.Tab) = Unit

        override fun onTabSelected(tab: TabLayout.Tab) {
            notifyChange(tab.position)
        }
    }
}

fun <Data> ObservableBindingConverter<Data, List<BarDataSet>?>.onChart(chart: Chart<*>)
        = on { BarData(it) }.toView(chart) { exp, value -> exp.data = value }

fun BarChart.appChart() {
    setFitBars(true)
    animateXY(2000, 2000)
    xAxis.labelCount = 0
    description = null

    arrayOf(axisLeft, axisRight).forEach {
        it.apply {
            axisMinimum = 0.0f
            axisMaximum = 1000.0f
        }
    }
}