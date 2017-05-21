package com.grosner.androiddatabaselibrarycomparison2.tests

import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.andrewgrosner.kbinding.BaseObservable
import com.andrewgrosner.kbinding.anko.BindingComponent
import com.andrewgrosner.kbinding.bindings.*
import com.andrewgrosner.kbinding.observable
import com.andrewgrosner.kbinding.viewextensions.text
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.grosner.androiddatabaselibrarycomparison2.R
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.linearLayoutCompat
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.support.v4.nestedScrollView

interface MainActivityComponentHandler {

    fun runSimpleTrial()

    fun runPerformanceTrial()

    fun runPerformanceTrial2()
}

class MainActivityViewModel : BaseObservable() {

    val isLoading = observable(false)

    val hasLoaded = observable(false)

    val runningDisplayText = observable("")

    val resultsLabel = observable("")

    val saveData = observable<List<BarDataSet>?>(null)

    val loadData = observable<List<BarDataSet>?>(null)

    val resultsCount = observable(0)

    val testIndex = observable(-1)
}

/**
 * Description:
 */
class MainActivityComponent(var componentHandler: MainActivityComponentHandler?)
    : BindingComponent<MainActivity, MainActivityViewModel>() {
    override fun createViewWithBindings(ui: AnkoContext<MainActivity>) = with(ui) {
        coordinatorLayout {
            appBarLayout {
                toolbar {
                    title = "Database Comparisons"
                    setTitleTextColor(Color.WHITE)
                }

                horizontalProgressBar {
                    bind { it.resultsCount }.on { ((it.toDouble() / MainActivity.TEST_COUNT.toDouble()) * 100).toInt() }
                            .toProgressBar(this)
                    bindSelf { it.isLoading }.toViewVisibilityB(this)
                }

            }.lparams(width = MATCH_PARENT, height = WRAP_CONTENT)
            nestedScrollView {
                coordinatorLParams(width = MATCH_PARENT, height = MATCH_PARENT,
                        behavior = AppBarLayout.ScrollingViewBehavior())
                verticalLayout {

                    linearLayoutCompat {
                        button {
                            text = text(R.string.simple)
                            bind { it.isLoading }.reverse().toView(this) { exp, value ->
                                value?.let { exp.isEnabled = value }
                            }
                            bind { it.testIndex }.on { it == 0 || it == -1 }
                                    .toViewVisibilityB(this)
                            setOnClickListener {
                                componentHandler?.runSimpleTrial()
                            }
                        }

                        button {
                            text = text(R.string.performance)
                            bind { it.isLoading }.reverse().toView(this) { exp, value ->
                                if (value != null) {
                                    exp.isEnabled = value
                                }
                            }
                            bind { it.testIndex }.on { it == 1 || it == -1 }
                                    .toViewVisibilityB(this)
                            setOnClickListener { componentHandler?.runPerformanceTrial() }
                        }

                        button {
                            text = text(R.string.performance2)
                            bind { it.isLoading }.reverse().toView(this) { exp, value ->
                                if (value != null) {
                                    exp.isEnabled = value
                                }
                            }
                            bind { it.testIndex }.on { it == 2 || it == -1 }
                                    .toViewVisibilityB(this)
                            setOnClickListener { componentHandler?.runPerformanceTrial2() }
                        }
                    }

                    textView {
                        bind { it.resultsCount }.on { "$it trials" }.toText(this)
                    }

                    textView {
                        bindSelf { it.resultsLabel }.toText(this)
                        bind { it.resultsLabel }.onIsNotNull().toViewVisibilityB(this)
                    }

                    textView {
                        bind { it.runningDisplayText }.onSelf().toText(this)
                    }

                    barChart {
                        bindSelf { it.hasLoaded }.toViewVisibilityB(this)
                        appChart()
                        bind { it.saveData }.on { BarData(it) }.toView(this) { exp, value ->
                            exp.data = value
                        }
                    }.lparams {
                        width = MATCH_PARENT
                        height = dip(300)
                    }

                    barChart {
                        bindSelf { it.hasLoaded }.toViewVisibilityB(this)
                        appChart()
                        bind { it.loadData }.on { BarData(it) }.toView(this) { exp, value ->
                            exp.data = value
                        }
                    }.lparams {
                        width = MATCH_PARENT
                        height = dip(300)
                    }
                }
            }
        }
    }

}

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