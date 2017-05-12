package com.grosner.androiddatabaselibrarycomparison2.dbflow

import com.grosner.androiddatabaselibrarycomparison2.tests.MainActivity
import com.grosner.androiddatabaselibrarycomparison2.events.LogTestDataEvent
import com.grosner.androiddatabaselibrarycomparison2.tests.randomPlayerList
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.sql.language.Delete
import org.greenrobot.eventbus.EventBus

val DBFLOW_FRAMEWORK_NAME = "DBFlow"

fun testDBFlow() {

    Delete.tables(Player::class.java)

    val list = randomPlayerList { Player() }

    var startTime = System.currentTimeMillis()
    databaseForTable<Player>().executeTransaction(list.fastInsert().build())
    EventBus.getDefault().post(LogTestDataEvent(startTime, DBFLOW_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    (select from Player::class).list
    EventBus.getDefault().post(LogTestDataEvent(startTime, DBFLOW_FRAMEWORK_NAME, MainActivity.LOAD_TIME))

    Delete.tables(Player::class.java)
}