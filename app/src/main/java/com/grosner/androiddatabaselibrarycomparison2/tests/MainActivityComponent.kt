package com.grosner.androiddatabaselibrarycomparison2.tests

import android.view.ViewGroup
import android.view.ViewManager
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
import org.jetbrains.anko.custom.ankoView

interface MainActivityComponentHandler {

    fun runSimpleTrial()

    fun runPerformanceTrial()

    fun runPerformanceTrial2();
}

class MainActivityViewModel : BaseObservable() {

    val isLoading = observable(false)

    val runningDisplayText = observable("")

    val resultsLabel = observable("")

    val saveData = observable<List<BarDataSet>?>(null)

    val loadData = observable<List<BarDataSet>?>(null)
}

/**
 * Description:
 */
class MainActivityComponent(var componentHandler: MainActivityComponentHandler?)
    : BindingComponent<MainActivity, MainActivityViewModel>() {
    override fun createViewWithBindings(ui: AnkoContext<MainActivity>) = with(ui) {
        verticalLayout {

            linearLayout {
                button {
                    text = text(R.string.simple)
                    bind { it.isLoading }.reverse().toView(this) { exp, value ->
                        if (value != null) {
                            exp.enabled = value
                        }
                    }
                    onClick { componentHandler?.runSimpleTrial() }
                }

                button {
                    text = text(R.string.performance)
                    bind { it.isLoading }.reverse().toView(this) { exp, value ->
                        if (value != null) {
                            exp.enabled = value
                        }
                    }
                    onClick { componentHandler?.runPerformanceTrial() }
                }

                button {
                    text = text(R.string.performance2)
                    bind { it.isLoading }.reverse().toView(this) { exp, value ->
                        if (value != null) {
                            exp.enabled = value
                        }
                    }
                    onClick { componentHandler?.runPerformanceTrial2() }
                }

                progressBar {
                    bind { it.isLoading }.onSelf().toViewVisibilityB(this)
                }
            }

            textView {
                bind { it.resultsLabel }.onSelf().toText(this)
                bind { it.resultsLabel }.onIsNotNull().toViewVisibilityB(this)
            }

            textView {
                bind { it.runningDisplayText }.onSelf().toText(this)
            }

            barChart {
                bind { it.isLoading }.reverse().toViewVisibilityB(this)
                setFitBars(true)
                animateXY(2000, 2000)
                description = null
                xAxis.labelCount = 0
                bind { it.saveData }.on { it?.let { BarData(it) } }.toView(this) { exp, value ->
                    exp.data = value
                }
            }.lparams {
                weight = 1.0f
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = 0
            }
            barChart {
                bind { it.isLoading }.reverse().toViewVisibilityB(this)
                setFitBars(true)
                animateXY(2000, 2000)
                xAxis.labelCount = 0
                description = null
                bind { it.loadData }.on { it?.let { BarData(it) } }.toView(this) { exp, value ->
                    exp.data = value
                }
            }.lparams {
                weight = 1.0f
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = 0
            }
        }
    }

}

inline fun ViewManager.barChart(init: BarChart.() -> Unit): BarChart {
    return ankoView({ BarChart(it) }, theme = 0, init = init)
}