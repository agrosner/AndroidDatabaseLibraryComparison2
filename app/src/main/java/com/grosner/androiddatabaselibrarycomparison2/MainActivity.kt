package com.grosner.androiddatabaselibrarycomparison2

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.grosner.androiddatabaselibrarycomparison2.dbflow.DBFLOW_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.dbflow.testDBFlow
import com.grosner.androiddatabaselibrarycomparison2.events.LogTestDataEvent
import com.grosner.androiddatabaselibrarycomparison2.events.TrialCompletedEvent
import com.grosner.androiddatabaselibrarycomparison2.greendao.GREENDAO_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.greendao.testGreenDao
import com.grosner.androiddatabaselibrarycomparison2.realm.REALM_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.realm.testRealmModels
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : Activity() {

    private lateinit var simpleTrialButton: Button
    private lateinit var complexTrialButton: Button
    private lateinit var resultsLabel: TextView
    private lateinit var resultsContainer: ScrollView
    private lateinit var resultsTextView: TextView
    private lateinit var chartView: BarChart
    private lateinit var progressBar: ProgressBar


    private var chartEntrySets = LinkedHashMap<String, ArrayList<BarEntry>>()
    private var runningTests = false
    private var runningTestName: String? = ""
    private var runTestThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        simpleTrialButton = findViewById(R.id.simple) as Button
        complexTrialButton = findViewById(R.id.complex) as Button
        resultsLabel = findViewById(R.id.resultsLabel) as TextView
        resultsContainer = findViewById(R.id.resultsContainer) as ScrollView
        resultsTextView = findViewById(R.id.results) as TextView
        progressBar = findViewById(R.id.progress) as ProgressBar
        progressBar.isIndeterminate = true
        chartView = findViewById(R.id.chart) as BarChart

        if (savedInstanceState != null) {
            runningTests = savedInstanceState.getBoolean(STATE_RUNNING_TESTS)
            runningTestName = savedInstanceState.getString(STATE_TEST_NAME)
            chartEntrySets = savedInstanceState.getSerializable(STATE_MAPDATA) as LinkedHashMap<String, ArrayList<BarEntry>>

            setBusyUI(runningTests, runningTestName ?: "")
            if (!runningTests && chartEntrySets.size > 0) {
                // graph existing data
                initChart()
            }
        }
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        with(savedInstanceState) {
            putBoolean(STATE_RUNNING_TESTS, runningTests)
            putString(STATE_TEST_NAME, runningTestName)
            putSerializable(STATE_MAPDATA, chartEntrySets)
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
    @Subscribe
    fun onEvent(event: LogTestDataEvent) {
        logTime(event.startTime, event.framework, event.eventName)
    }

    // handle graphing event
    @Subscribe(threadMode = ThreadMode.MAIN)
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
        runOnUiThread { resultsTextView.text = resultsStringBuilder.toString() }
        // update chart data
        val entry = BarEntry((if (name == SAVE_TIME) 0 else 1).toFloat(), elapsedMsec.toFloat())
        chartEntrySets.getValue(framework).add(entry);
    }

    private fun setBusyUI(enabled: Boolean, testName: String) {
        runningTestName = testName
        runningTests = enabled
        if (enabled) {
            runningTests = true
            resultsStringBuilder.setLength(0)
            resultsContainer.visibility = View.VISIBLE
            chartView.visibility = View.GONE
            enableButtons(false)
            progressBar.visibility = View.VISIBLE
        } else {
            runningTests = false
            resultsContainer.visibility = View.GONE
            if (runningTestName != null) {
                chartView.visibility = View.VISIBLE
            }
            enableButtons(true)
            progressBar.visibility = View.GONE
        }
        if (runningTestName != null) {
            resultsLabel.text = resources.getString(R.string.results, testName)
            resultsLabel.visibility = View.VISIBLE
        }
    }

    private fun initChart() {
        val dataSets = chartEntrySets.keys.map {
            BarDataSet(chartEntrySets[it], it).apply { color = getFrameworkColor(it) }
        }
        with(chartView) {
            this.data = BarData(dataSets)
            setFitBars(true)
            description = null // this takes up t`oo much space, so clear it
            animateXY(2000, 2000)
            invalidate()
        }
    }

    private fun resetChart() {
        with(chartEntrySets) {
            clear()
            // the order you add these in is the order they're displayed in
            put(DBFLOW_FRAMEWORK_NAME, arrayListOf<BarEntry>())
            put(GREENDAO_FRAMEWORK_NAME, arrayListOf<BarEntry>())
            put(REALM_FRAMEWORK_NAME, arrayListOf<BarEntry>())
        }
    }

    private fun getFrameworkColor(framework: String): Int {
        // using the 300 line colors from http://www.google.com/design/spec/style/color.html#color-color-palette
        when (framework) {
            DBFLOW_FRAMEWORK_NAME -> return Color.rgb(0xE5, 0x73, 0x73) // red
            GREENDAO_FRAMEWORK_NAME -> return Color.rgb(0xBA, 0x68, 0xC8) // purple
            REALM_FRAMEWORK_NAME -> return Color.rgb(0xAE, 0xD5, 0X81); // light green
            else -> return Color.WHITE
        }
    }

    private fun enableButtons(enabled: Boolean) {
        simpleTrialButton.isEnabled = enabled
        complexTrialButton.isEnabled = enabled
    }

    /**
     * runs simple benchmarks (onClick from R.id.simple)

     * @param v button view
     */
    fun runSimpleTrial(v: View) {
        setBusyUI(true, resources.getString(R.string.simple))
        resetChart()
        runTestThread = Thread(Runnable {
            runningTests = true
            val applicationContext = this@MainActivity.applicationContext
            testDBFlow()
            testGreenDao(applicationContext)
            testRealmModels()
            EventBus.getDefault().post(TrialCompletedEvent(resources.getString(R.string.simple)))
        }).apply { start() }
    }

    /**
     * runs complex benchmarks (onClick from R.id.complex)

     * @param v button view
     */
    fun runComplexTrial(v: View) {
        setBusyUI(true, resources.getString(R.string.complex))
        resetChart()
        Thread(Runnable {
            runningTests = true
            val applicationContext = this@MainActivity.applicationContext
            /*OrmLiteTester.testAddressBooks(applicationContext);
                GreenDaoTester.testAddressBooks(applicationContext);
                DBFlowTester.testAddressBooks(applicationContext);
                OllieTester.testAddressBooks(applicationContext);
                RealmTester.testAddressBooks(applicationContext);*/
            //SprinklesTester.testAddressBooks(applicationContext);
            //AATester.testAddressBooks(applicationContext);
            //SugarTester.testAddressBooks(applicationContext);
            EventBus.getDefault().post(TrialCompletedEvent(resources.getString(R.string.complex)))
        }).start()
    }

    companion object {
        val LOAD_TIME = "Load"
        val SAVE_TIME = "Save"

        const val LOOP_COUNT = 5000

        private val STATE_MAPDATA = "mapData"
        private val STATE_RUNNING_TESTS = "runningTests"
        private val STATE_TEST_NAME = "testName"
        private val resultsStringBuilder = StringBuilder()
    }
}
