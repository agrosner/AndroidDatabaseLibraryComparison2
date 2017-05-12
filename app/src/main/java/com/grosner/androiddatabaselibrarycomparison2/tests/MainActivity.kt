package com.grosner.androiddatabaselibrarycomparison2.tests

import android.app.Activity
import android.graphics.Color.WHITE
import android.graphics.Color.rgb
import android.util.Log
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.grosner.androiddatabaselibrarycomparison2.R
import com.grosner.androiddatabaselibrarycomparison2.dbflow.DBFLOW_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.dbflow.testDBFlow
import com.grosner.androiddatabaselibrarycomparison2.dbflow.testDBFlowPerformance
import com.grosner.androiddatabaselibrarycomparison2.dbflow.testDBFlowPerformance2
import com.grosner.androiddatabaselibrarycomparison2.events.LogTestDataEvent
import com.grosner.androiddatabaselibrarycomparison2.events.TrialCompletedEvent
import com.grosner.androiddatabaselibrarycomparison2.greendao.GREENDAO_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.greendao.testGreenDao
import com.grosner.androiddatabaselibrarycomparison2.greendao.testGreenDaoPerformance2
import com.grosner.androiddatabaselibrarycomparison2.realm.REALM_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.realm.testRealmModels
import com.grosner.androiddatabaselibrarycomparison2.realm.testRealmModelsPerformance2
import com.grosner.androiddatabaselibrarycomparison2.requery.REQUERY_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.requery.testRequery
import com.grosner.androiddatabaselibrarycomparison2.requery.testRequeryPerformance
import com.grosner.androiddatabaselibrarycomparison2.requery.testRequeryPerformance2
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.setContentView

class MainActivity : MainActivityComponentHandler, Activity() {

    private var chartSave = arrayListOf<BarEntry>()
    private var chartLoad = arrayListOf<BarEntry>()
    private var runningTests = false
    private var runningTestName: String? = ""
    private var runTestThread: Thread? = null
    private val resultsStringBuilder = StringBuilder()

    private val viewModel = MainActivityViewModel()

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityComponent(this).apply {
            viewModel = this@MainActivity.viewModel
        }.setContentView(this)

        if (savedInstanceState != null) {
            runningTests = savedInstanceState.getBoolean(STATE_RUNNING_TESTS)
            runningTestName = savedInstanceState.getString(STATE_TEST_NAME)
            chartSave = savedInstanceState.getSerializable(STATE_SAVE_DATA) as ArrayList<BarEntry>
            chartLoad = savedInstanceState.getSerializable(STATE_LOAD_DATA) as ArrayList<BarEntry>

            setBusyUI(runningTests, runningTestName ?: "")
            if (!runningTests && chartSave.size > 0) {
                // graph existing data
                initChart()
            }
        }
    }

    public override fun onSaveInstanceState(savedInstanceState: android.os.Bundle) {
        with(savedInstanceState) {
            putBoolean(Companion.STATE_RUNNING_TESTS, runningTests)
            putString(Companion.STATE_TEST_NAME, runningTestName)
            putSerializable(Companion.STATE_LOAD_DATA, chartSave)
        }

        super.onSaveInstanceState(savedInstanceState)
    }


    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    // handle data collection event
    @org.greenrobot.eventbus.Subscribe
    fun onEvent(event: LogTestDataEvent) {
        logTime(event.startTime, event.framework, event.eventName)
    }

    // handle graphing event
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    fun onEventMainThread(event: TrialCompletedEvent) {
        initChart()
        runningTests = false
        setBusyUI(false, event.trialName)
    }

    /**
     * Logs msec between start time and now

     * @param startTime relative to start time in msec; use -1 to set elapsed time to zero
     * *
     * @param framework framework logging event
     * *
     * @param name      string to log for event
     */
    fun logTime(startTime: Long, framework: String, name: String) {
        Log.e(MainActivity::class.java.simpleName, name + " took: " + (System.currentTimeMillis() - startTime))
        val elapsedMsec = if (startTime == -1L) 0 else System.currentTimeMillis() - startTime
        resultsStringBuilder.append("$framework $name took: $elapsedMsec msec\n")
        runOnUiThread { viewModel.runningDisplayText.value = resultsStringBuilder.toString() }
        // update chart data
        val position = when (framework) {
            DBFLOW_FRAMEWORK_NAME -> 0
            REALM_FRAMEWORK_NAME -> 1
            REQUERY_FRAMEWORK_NAME -> 2
            GREENDAO_FRAMEWORK_NAME -> 3
            else -> 4
        }
        val entry = BarEntry(position.toFloat(), elapsedMsec.toFloat())
        entry.data = framework
        if (name == SAVE_TIME) {
            chartSave.add(entry)
        } else {
            chartLoad.add(entry)
        }
    }

    private fun setBusyUI(enabled: Boolean, testName: String) {
        runningTestName = testName
        runningTests = enabled
        viewModel.isLoading.value = enabled
        if (enabled) {
            resultsStringBuilder.setLength(0)
        }
        if (runningTestName != null) {
            viewModel.resultsLabel.value = resources.getString(R.string.results, testName)
        }
    }

    private fun initChart() {
        viewModel.saveData.value = chartSave.map { BarDataSet(listOf(it), it.data.toString()).apply { color = getFrameworkColor(it.data.toString()) } }
        viewModel.loadData.value = chartLoad.map { BarDataSet(listOf(it), it.data.toString()).apply { color = getFrameworkColor(it.data.toString()) } }
    }

    private fun resetChart() {
        with(chartSave) {
            clear()
        }
        with(chartLoad) {
            clear()
        }
    }

    private fun getFrameworkColor(framework: String): Int {
        // using the 300 line colors from http://www.google.com/design/spec/style/color.html#color-color-palette
        when (framework) {
            DBFLOW_FRAMEWORK_NAME -> return rgb(0xE5, 0x73, 0x73) // red
            GREENDAO_FRAMEWORK_NAME -> return rgb(0xBA, 0x68, 0xC8) // purple
            REALM_FRAMEWORK_NAME -> return rgb(0xAE, 0xD5, 0X81); // light green
            REQUERY_FRAMEWORK_NAME -> return rgb(0x79, 0x86, 0xCB) // indigo
            else -> return WHITE
        }
    }

    /**
     * runs simple benchmarks (onClick from R.id.simple)

     * @param v button view
     */
    override fun runSimpleTrial() {
        setBusyUI(true, resources.getString(com.grosner.androiddatabaselibrarycomparison2.R.string.simple))
        resetChart()
        runTestThread = Thread(Runnable {
            runningTests = true
            val applicationContext = this@MainActivity.applicationContext
            testDBFlow()
            testRealmModels()
            testRequery(applicationContext)
            testGreenDao(applicationContext)
            EventBus.getDefault().post(TrialCompletedEvent(resources.getString(R.string.simple)))
        }).apply { start() }
    }

    override fun runPerformanceTrial() {
        setBusyUI(true, resources.getString(R.string.performance))
        resetChart()
        runTestThread = Thread(Runnable {
            runningTests = true
            val applicationContext = this@MainActivity.applicationContext
            testDBFlowPerformance()
            testRealmModels()
            testRequeryPerformance(applicationContext)
            testGreenDao(applicationContext)
            EventBus.getDefault().post(TrialCompletedEvent(resources.getString(R.string.performance)))
        }).apply { start() }
    }

    override fun runPerformanceTrial2() {
        setBusyUI(true, resources.getString(R.string.performance2))
        resetChart()
        runTestThread = Thread(Runnable {
            runningTests = true
            val applicationContext = this@MainActivity.applicationContext
            testDBFlowPerformance2()
            testRealmModelsPerformance2()
            testRequeryPerformance2(applicationContext)
            testGreenDaoPerformance2(applicationContext)
            EventBus.getDefault().post(TrialCompletedEvent(resources.getString(R.string.performance)))
        }).apply { start() }
    }

    companion object {
        val LOAD_TIME = "Load"
        val SAVE_TIME = "Save"

        const val LOOP_COUNT = 5000

        private val STATE_LOAD_DATA = "loadData"
        private val STATE_SAVE_DATA = "saveData"
        private val STATE_RUNNING_TESTS = "runningTests"
        private val STATE_TEST_NAME = "testName"
    }
}
