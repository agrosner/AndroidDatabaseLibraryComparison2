package com.grosner.androiddatabaselibrarycomparison2.tests

import android.app.Activity
import android.graphics.Color
import android.graphics.Color.WHITE
import android.graphics.Color.rgb
import android.util.Log
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.grosner.androiddatabaselibrarycomparison2.R
import com.grosner.androiddatabaselibrarycomparison2.dbflow.DBFLOW_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.dbflow.DBFlowTest
import com.grosner.androiddatabaselibrarycomparison2.dbflow.DBFlowTestPerformance
import com.grosner.androiddatabaselibrarycomparison2.dbflow.DBFlowTestPerformance2
import com.grosner.androiddatabaselibrarycomparison2.greendao.GREENDAO_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.greendao.GreenDaoTest
import com.grosner.androiddatabaselibrarycomparison2.greendao.GreenDaoTestPerformance2
import com.grosner.androiddatabaselibrarycomparison2.raw.RAW_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.raw.RawTest
import com.grosner.androiddatabaselibrarycomparison2.raw.RawTestPerformance2
import com.grosner.androiddatabaselibrarycomparison2.realm.REALM_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.realm.RealmDefault
import com.grosner.androiddatabaselibrarycomparison2.realm.RealmPerformance
import com.grosner.androiddatabaselibrarycomparison2.requery.REQUERY_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.requery.RequeryTest
import com.grosner.androiddatabaselibrarycomparison2.requery.RequeryTestPerformance
import com.grosner.androiddatabaselibrarycomparison2.requery.RequeryTestPerformance2
import com.grosner.androiddatabaselibrarycomparison2.room.ROOM_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.room.RoomTest
import org.jetbrains.anko.setContentView

class MainActivity : MainActivityComponentHandler, Activity() {

    private var chartSave = arrayListOf<BarEntry>()
    private var chartLoad = arrayListOf<BarEntry>()
    private var runningTests = false
    private var runningTestName: String? = ""
    private var runTestThread: Thread? = null
    private val resultsStringBuilder = StringBuilder()

    private var resultMap = mutableMapOf<String, MutableSet<Result>>()

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


    fun trialCompleted(trialName: String) {

        resultMap.forEach { name, _ ->
            val position = when (name) {
                DBFLOW_FRAMEWORK_NAME -> 0
                ROOM_FRAMEWORK_NAME -> 1
                REALM_FRAMEWORK_NAME -> 2
                REQUERY_FRAMEWORK_NAME -> 3
                GREENDAO_FRAMEWORK_NAME -> 4
                RAW_FRAMEWORK_NAME -> 5
                else -> 6
            }

            val (_, insertTime, loadTime) = calculateAverage(name)

            resultsStringBuilder.append("$name AVG: $insertTime / $loadTime msec\n")

            chartSave.add(BarEntry(position.toFloat(), insertTime.toFloat()).apply { data = name })
            chartLoad.add(BarEntry(position.toFloat(), loadTime.toFloat()).apply { data = name })
        }

        viewModel.runningDisplayText.value = resultsStringBuilder.toString()
        viewModel.hasLoaded.value = true
        initChart()
        runningTests = false
        setBusyUI(false, trialName)
    }

    fun calculateAverage(framework: String): Result {

        val set = resultMap.getValue(framework)

        var insertTime: Double = 0.0
        var loadTime: Double = 0.0
        set.forEach {
            loadTime += it.loadTime
            insertTime += it.insertTime
        }
        val testCount = set.size.toDouble()
        insertTime /= testCount
        loadTime /= testCount
        return Result(framework, insertTime.toLong(), loadTime.toLong())
    }

    /**
     * Logs msec between start time and now

     * @param startTime relative to start time in msec; use -1 to set elapsed time to zero
     * *
     * @param framework framework logging event
     * *
     * @param name      string to log for event
     */
    fun logTime(result: Result) {
        val (name) = result
        Log.e(MainActivity::class.java.simpleName, name + " took: " + (result.totalTime))
        // update chart data
        var set: MutableSet<Result>? = resultMap[name]
        if (set == null) {
            set = mutableSetOf<Result>()
            resultMap[name] = set
        }
        set.add(result)

    }

    private fun setBusyUI(enabled: Boolean, testName: String) {
        runningTestName = testName
        runningTests = enabled
        viewModel.apply {
            currentTest.value = testName
            isLoading.value = enabled
            if (enabled) {
                hasLoaded.value = false
                resultsStringBuilder.setLength(0)
            }
            resultsLabel.value = resources.getString(R.string.results, testName)
        }
    }

    private fun initChart() {
        viewModel.saveData.value = chartSave.map {
            BarDataSet(listOf(it), it.data.toString())
                    .apply { color = getFrameworkColor(it.data.toString()) }
        }
        viewModel.loadData.value = chartLoad.map {
            BarDataSet(listOf(it), it.data.toString())
                    .apply { color = getFrameworkColor(it.data.toString()) }
        }

    }

    private fun resetChart() {
        chartSave.clear()
        chartLoad.clear()
        resultMap.clear()
        viewModel.resultsCount.value = 0
    }

    private fun getFrameworkColor(framework: String): Int {
        // using the 300 line colors from http://www.google.com/design/spec/style/color.html#color-color-palette
        when (framework) {
            DBFLOW_FRAMEWORK_NAME -> return rgb(0xE5, 0x73, 0x73) // red
            GREENDAO_FRAMEWORK_NAME -> return rgb(0xBA, 0x68, 0xC8) // purple
            REALM_FRAMEWORK_NAME -> return rgb(0xAE, 0xD5, 0X81); // light green
            REQUERY_FRAMEWORK_NAME -> return rgb(0x79, 0x86, 0xCB) // indigo
            RAW_FRAMEWORK_NAME -> return Color.YELLOW
            ROOM_FRAMEWORK_NAME -> return Color.GRAY
            else -> return WHITE
        }
    }

    fun logTrial() {
        viewModel.resultsCount.value = viewModel.resultsCount.value + 1
    }

    /**
     * runs simple benchmarks (onClick from R.id.simple)

     * @param v button view
     */
    override fun runSimpleTrial() {
        runTest(resources.getString(R.string.simple), DBFlowTest(),
                RoomTest(applicationContext),
                RealmDefault(),
                RequeryTest(applicationContext),
                GreenDaoTest(applicationContext),
                RawTest(applicationContext))
    }

    override fun runPerformanceTrial() {
        runTest(resources.getString(R.string.performance), DBFlowTestPerformance(),
                RoomTest(applicationContext),
                RealmDefault(),
                RequeryTestPerformance(applicationContext),
                GreenDaoTest(applicationContext),
                RawTest(applicationContext))
    }

    override fun runPerformanceTrial2() {
        runTest(resources.getString(R.string.performance2),
                DBFlowTestPerformance2(),
                RoomTest(applicationContext),
                RealmPerformance(),
                RequeryTestPerformance2(applicationContext),
                GreenDaoTestPerformance2(applicationContext),
                RawTestPerformance2(applicationContext))
    }

    fun runTest(testName: String, vararg tests: BaseTest<*>) {
        setBusyUI(true, testName)
        resetChart()
        runTestThread = Thread(Runnable {
            runningTests = true
            (0 until TEST_COUNT).forEach {
                tests.forEach { logTime(it.test()) }
                logTrial()
            }
            trialCompleted(testName)
        }).apply { start() }
    }

    companion object {
        const val LOOP_COUNT = 5000
        const val TEST_COUNT = 15

        private val STATE_LOAD_DATA = "loadData"
        private val STATE_SAVE_DATA = "saveData"
        private val STATE_RUNNING_TESTS = "runningTests"
        private val STATE_TEST_NAME = "testName"
    }
}
