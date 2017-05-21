package com.grosner.androiddatabaselibrarycomparison2.tests

import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewManager
import com.github.mikephil.charting.charts.BarChart
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