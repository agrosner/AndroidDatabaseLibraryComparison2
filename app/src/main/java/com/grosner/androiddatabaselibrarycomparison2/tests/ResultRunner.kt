package com.grosner.androiddatabaselibrarycomparison2.tests

import android.arch.lifecycle.LiveData
import android.graphics.Color
import android.util.Log
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.grosner.androiddatabaselibrarycomparison2.dbflow.DBFLOW_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.greendao.GREENDAO_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.raw.RAW_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.realm.REALM_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.requery.REQUERY_FRAMEWORK_NAME
import com.grosner.androiddatabaselibrarycomparison2.room.ROOM_FRAMEWORK_NAME

/**
 * Description:
 */
class ResultRunner : LiveData<ResultRunner>() {

    interface ResultHandler {

        fun logTrial()

        fun trialCompleted(trialName: String, resultRunner: ResultRunner)
    }

    var resultHandler: ResultHandler? = null

    private var resultMap = mutableMapOf<String, MutableSet<Result>>()

    private var chartSave = arrayListOf<BarEntry>()
    private var chartLoad = arrayListOf<BarEntry>()
    private var runTestThread: Thread? = null

    private var runningTests = false

    var hasLoaded = false

    private val resultsStringBuilder = StringBuilder()

    val chartSaveDataSet
        get() = mapChartData(chartSave)

    val chartLoadDataSet
        get() = mapChartData(chartLoad)

    val resultsRaw
        get() = resultsStringBuilder.toString()

    init {
        value = this
    }

    fun startTrial() {
        runningTests = true
        resultsStringBuilder.setLength(0)
        chartSave.clear()
        chartLoad.clear()
        resultMap.clear()
    }

    fun trialCompleted(trialName: String) {
        hasLoaded = true
        runningTests = false
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

        resultHandler?.trialCompleted(trialName, this)
    }

    fun runTest(testName: String, vararg tests: BaseTest<*>) {
        startTrial()
        runTestThread = Thread(Runnable {
            runningTests = true
            (0 until MainActivity.TEST_COUNT).forEach {
                tests.forEach { logTime(it.test()) }
                resultHandler?.logTrial()
            }
            trialCompleted(testName)
        }).apply { start() }
    }

    override fun onInactive() {
        // do something here?
    }

    private fun mapChartData(entryList: List<BarEntry>) = entryList.map {
        BarDataSet(listOf(it), it.data.toString())
                .apply { color = getFrameworkColor(it.data.toString()) }
    }

    /**
     * Logs msec between start time and now

     * @param startTime relative to start time in msec; use -1 to set elapsed time to zero
     * *
     * @param framework framework logging event
     * *
     * @param name      string to log for event
     */
    private fun logTime(result: Result) {
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

    private fun calculateAverage(framework: String): Result {

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

    private fun getFrameworkColor(framework: String): Int {
        // using the 300 line colors from http://www.google.com/design/spec/style/color.html#color-color-palette
        when (framework) {
            DBFLOW_FRAMEWORK_NAME -> return Color.rgb(0xE5, 0x73, 0x73) // red
            GREENDAO_FRAMEWORK_NAME -> return Color.rgb(0xBA, 0x68, 0xC8) // purple
            REALM_FRAMEWORK_NAME -> return Color.rgb(0xAE, 0xD5, 0X81); // light green
            REQUERY_FRAMEWORK_NAME -> return Color.rgb(0x79, 0x86, 0xCB) // indigo
            RAW_FRAMEWORK_NAME -> return Color.YELLOW
            ROOM_FRAMEWORK_NAME -> return Color.GRAY
            else -> return Color.WHITE
        }
    }
}