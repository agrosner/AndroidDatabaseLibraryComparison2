package com.grosner.androiddatabaselibrarycomparison2.raw

import android.content.Context
import com.grosner.androiddatabaselibrarycomparison2.events.LogTestDataEvent
import com.grosner.androiddatabaselibrarycomparison2.raw.PlayerContract.PlayerEntry.TABLE_NAME
import com.grosner.androiddatabaselibrarycomparison2.tests.MainActivity
import com.grosner.androiddatabaselibrarycomparison2.tests.randomPlayerList
import org.greenrobot.eventbus.EventBus

val RAW_FRAMEWORK_NAME = "Raw SQLite"

fun testRawModels(context: Context) {
    val helper = PlayerDBHelper(context)

    val players = randomPlayerList { Player() }

    val db = helper.writableDatabase

    db.execSQL("DELETE FROM $TABLE_NAME")

    var startTime = System.currentTimeMillis()
    db.beginTransaction()
    try {
        players.forEach {
            db.insert(TABLE_NAME, null, it.getInsertValues())
        }
        db.setTransactionSuccessful()
    } finally {
        db.endTransaction()
    }

    EventBus.getDefault().post(LogTestDataEvent(startTime, RAW_FRAMEWORK_NAME, MainActivity.SAVE_TIME))


    startTime = System.currentTimeMillis()

    val items = arrayListOf<Player>()
    db.rawQuery("SELECT * FROM $TABLE_NAME", null).use {
        while (it.moveToNext()) {
            items += Player.fromCursor(it)
        }
    }

    EventBus.getDefault().post(LogTestDataEvent(startTime, RAW_FRAMEWORK_NAME, MainActivity.LOAD_TIME))

    db.execSQL("DELETE FROM $TABLE_NAME")

    db.close()
}