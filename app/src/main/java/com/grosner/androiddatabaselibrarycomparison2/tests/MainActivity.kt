package com.grosner.androiddatabaselibrarycomparison2.tests

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import com.grosner.androiddatabaselibrarycomparison2.R
import com.grosner.androiddatabaselibrarycomparison2.dbflow.DBFlowTest
import com.grosner.androiddatabaselibrarycomparison2.dbflow.DBFlowTestPerformance
import com.grosner.androiddatabaselibrarycomparison2.dbflow.DBFlowTestPerformance2
import com.grosner.androiddatabaselibrarycomparison2.greendao.GreenDaoTest
import com.grosner.androiddatabaselibrarycomparison2.greendao.GreenDaoTestPerformance2
import com.grosner.androiddatabaselibrarycomparison2.raw.RawTest
import com.grosner.androiddatabaselibrarycomparison2.raw.RawTestPerformance2
import com.grosner.androiddatabaselibrarycomparison2.realm.RealmDefault
import com.grosner.androiddatabaselibrarycomparison2.realm.RealmPerformance
import com.grosner.androiddatabaselibrarycomparison2.requery.RequeryTest
import com.grosner.androiddatabaselibrarycomparison2.requery.RequeryTestPerformance
import com.grosner.androiddatabaselibrarycomparison2.requery.RequeryTestPerformance2
import com.grosner.androiddatabaselibrarycomparison2.room.RoomTest
import org.jetbrains.anko.setContentView

class MainActivity : MainActivityComponentHandler, LifecycleActivity() {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this)
                .get(MainActivityViewModel::class.java)

        MainActivityComponent(this).apply {
            viewModel = this@MainActivity.viewModel
        }.setContentView(this)

        viewModel.runner.observe(this, Observer {
            it?.let { viewModel.bindToRunner(it) }
        })
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
        viewModel.setBusyUI(true, testName)
        viewModel.runner.value?.runTest(testName, *tests)
    }

    companion object {
        const val LOOP_COUNT = 5000
        const val TEST_COUNT = 15
    }
}
