package com.grosner.androiddatabaselibrarycomparison2.tests

import android.arch.lifecycle.ViewModel
import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.andrewgrosner.kbinding.anko.BindingComponent
import com.andrewgrosner.kbinding.bindings.*
import com.andrewgrosner.kbinding.observable
import com.andrewgrosner.kbinding.viewextensions.string
import com.andrewgrosner.kbinding.viewextensions.text
import com.github.mikephil.charting.data.BarDataSet
import com.grosner.androiddatabaselibrarycomparison2.R
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.linearLayoutCompat
import org.jetbrains.anko.appcompat.v7.themedTintedButton
import org.jetbrains.anko.appcompat.v7.themedTintedTextView
import org.jetbrains.anko.appcompat.v7.themedToolbar
import org.jetbrains.anko.design.themedAppBarLayout
import org.jetbrains.anko.design.themedCoordinatorLayout
import org.jetbrains.anko.design.themedTabLayout
import org.jetbrains.anko.support.v4.themedNestedScrollView

interface MainActivityComponentHandler {

    fun runSimpleTrial()

    fun runPerformanceTrial()

    fun runPerformanceTrial2()

}

class MainActivityViewModel : ResultRunner.ResultHandler, ViewModel() {

    val isLoading = observable(false)

    val hasLoaded = observable(false)

    val runningDisplayText = observable("")

    val saveData = observable<List<BarDataSet>?>(null)
    val loadData = observable<List<BarDataSet>?>(null)

    val resultsCount = observable(0)

    val testIndex = observable(-1)

    val chartIndex = observable(0)

    val currentTest = observable("")

    val runner = ResultRunner().apply {
        resultHandler = this@MainActivityViewModel
    }

    fun bindToRunner(resultRunner: ResultRunner) {
        runningDisplayText.value = resultRunner.resultsRaw
        saveData.value = resultRunner.chartSaveDataSet
        loadData.value = resultRunner.chartLoadDataSet
        hasLoaded.value = resultRunner.hasLoaded
    }

    fun setBusyUI(enabled: Boolean, testName: String) {
        if (enabled) {
            runner.value?.startTrial()
        }
        currentTest.value = testName
        isLoading.value = enabled
        if (enabled) {
            hasLoaded.value = false
            runningDisplayText.value = "Awaiting Results"
            resultsCount.value = 0
        }
    }

    override fun trialCompleted(trialName: String, resultRunner: ResultRunner) {
        bindToRunner(resultRunner)
        setBusyUI(false, trialName)
    }


    override fun logTrial() {
        resultsCount.value++
    }

}

/**
 * Description:
 */
class MainActivityComponent(var componentHandler: MainActivityComponentHandler?)
    : BindingComponent<MainActivity, MainActivityViewModel>() {
    override fun createViewWithBindings(ui: AnkoContext<MainActivity>) = with(ui) {
        themedCoordinatorLayout {
            themedAppBarLayout {
                themedToolbar {
                    title = "Database Comparisons"
                    setTitleTextColor(Color.WHITE)
                }

                horizontalProgressBar {
                    bind { it.resultsCount }.on { ((it.toDouble() / MainActivity.TEST_COUNT.toDouble()) * 100).toInt() }
                            .toProgressBar(this)
                    bindSelf { it.isLoading }.toViewVisibilityB(this)
                }

            }.lparams(width = MATCH_PARENT, height = WRAP_CONTENT)
            themedNestedScrollView {
                coordinatorLParams(width = MATCH_PARENT, height = MATCH_PARENT,
                        behavior = AppBarLayout.ScrollingViewBehavior())
                verticalLayout {

                    themedTintedTextView {
                        text = text(R.string.TestHeader)
                        textSize = 18.0f
                    }

                    linearLayoutCompat {
                        themedTintedTextView {
                            bindSelf { it.isLoading }.toViewVisibilityB(this)
                            bind { it.currentTest }.on { "Running: $it" }.toText(this)
                            textSize = 18.0f
                        }

                        themedTintedButton {
                            text = text(R.string.simple)
                            bind { it.isLoading }.reverse().toViewVisibilityB(this)
                            bind { it.testIndex }.on { it == 0 || it == -1 }
                                    .toViewVisibilityB(this)
                            setOnClickListener { componentHandler?.runSimpleTrial() }
                        }

                        themedTintedButton {
                            text = text(R.string.performance)
                            bind { it.isLoading }.reverse().toViewVisibilityB(this)
                            bind { it.testIndex }.on { it == 1 || it == -1 }
                                    .toViewVisibilityB(this)
                            setOnClickListener { componentHandler?.runPerformanceTrial() }
                        }

                        themedTintedButton {
                            text = text(R.string.performance2)
                            bind { it.isLoading }.reverse().toViewVisibilityB(this)
                            bind { it.testIndex }.on { it == 2 || it == -1 }
                                    .toViewVisibilityB(this)
                            setOnClickListener { componentHandler?.runPerformanceTrial2() }
                        }
                    }.lparams {
                        bottomMargin = dip(16)
                    }

                    themedTintedTextView {
                        textSize = 18.0f
                        bind { it.currentTest }.on { string(R.string.results, it) }.toText(this)
                        bind { it.currentTest }.onIsNotNullOrEmpty().toViewVisibilityB(this)
                    }

                    themedTintedButton {
                        bindSelf { it.hasLoaded }.toViewVisibilityB(this)
                        text = text(R.string.DisplayResultsText)
                        setOnClickListener {
                            viewModel?.let { viewModel ->
                                alert(message = viewModel.runningDisplayText.value) {
                                    positiveButton(android.R.string.ok) { it.dismiss() }
                                }.build().show()
                            }
                        }
                    }.lparams(width = WRAP_CONTENT) {
                        bottomMargin = dip(16)
                    }

                    linearLayoutCompat {
                        orientation = LinearLayout.VERTICAL
                        bindSelf { it.hasLoaded }.toViewVisibilityB(this)
                        themedTabLayout {
                            addTab(newTab().apply {
                                text = "Insert"
                            })
                            addTab(newTab().apply {
                                text = "Load"
                            })
                            bindSelf { it.chartIndex }.toTabLayout(this)
                                    .twoWay()
                                    .toFieldFromTabLayout()
                        }

                        barChart {
                            bind { it.chartIndex }.on { it == 0 }.toViewVisibilityB(this)
                            appChart()
                            bind { it.saveData }.onChart(this)
                        }.lparams {
                            width = MATCH_PARENT
                            height = dip(300)
                        }

                        barChart {
                            bind { it.chartIndex }.on { it == 1 }.toViewVisibilityB(this)
                            appChart()
                            bind { it.loadData }.onChart(this)
                        }.lparams {
                            width = MATCH_PARENT
                            height = dip(300)
                        }

                    }
                }
            }
        }
    }

}