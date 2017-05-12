package com.grosner.androiddatabaselibrarycomparison2.dbflow

import com.grosner.androiddatabaselibrarycomparison2.events.LogTestDataEvent
import com.grosner.androiddatabaselibrarycomparison2.tests.MainActivity
import com.grosner.androiddatabaselibrarycomparison2.tests.printToString
import com.grosner.androiddatabaselibrarycomparison2.tests.randomPlayerList
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.sql.language.Delete
import org.greenrobot.eventbus.EventBus

val DBFLOW_FRAMEWORK_NAME = "DBFlow"

fun testDBFlow() {

    Delete.tables(Player::class.java)

    val list = randomPlayerList { Player() }
    modelAdapter<Player>().modelCache.clear()

    var startTime = System.currentTimeMillis()
    databaseForTable<Player>().executeTransaction {
        modelAdapter<Player>().insertAll(list)
    }
    EventBus.getDefault().post(LogTestDataEvent(startTime, DBFLOW_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    (select from Player::class).list
    EventBus.getDefault().post(LogTestDataEvent(startTime, DBFLOW_FRAMEWORK_NAME, MainActivity.LOAD_TIME))

    Delete.tables(Player::class.java)
    modelAdapter<Player>().modelCache.clear()
}

fun testDBFlowPerformance() {

    Delete.tables(Player2::class.java)

    val list = randomPlayerList { Player2() }

    var startTime = System.currentTimeMillis()
    databaseForTable<Player2>().executeTransaction {
        modelAdapter<Player2>().insertAll(list)
    }
    EventBus.getDefault().post(LogTestDataEvent(startTime, DBFLOW_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    (select from Player2::class).list
    EventBus.getDefault().post(LogTestDataEvent(startTime, DBFLOW_FRAMEWORK_NAME, MainActivity.LOAD_TIME))

    Delete.tables(Player::class.java)
}

fun testDBFlowPerformance2() {

    Delete.tables(Player2::class.java)

    val list = randomPlayerList { Player2() }

    var startTime = System.currentTimeMillis()
    databaseForTable<Player2>().executeTransaction {
        modelAdapter<Player2>().insertAll(list)
    }
    EventBus.getDefault().post(LogTestDataEvent(startTime, DBFLOW_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    (select from Player2::class).cursorResult.use {
        it.iterator().forEach {
            it.printToString
        }
    }
    EventBus.getDefault().post(LogTestDataEvent(startTime, DBFLOW_FRAMEWORK_NAME, MainActivity.LOAD_TIME))

    Delete.tables(Player::class.java)
}