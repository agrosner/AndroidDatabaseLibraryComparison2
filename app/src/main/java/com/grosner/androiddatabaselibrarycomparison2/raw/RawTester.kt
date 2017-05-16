package com.grosner.androiddatabaselibrarycomparison2.raw

import android.content.Context
import com.grosner.androiddatabaselibrarycomparison2.raw.PlayerContract.PlayerEntry.TABLE_NAME
import com.grosner.androiddatabaselibrarycomparison2.tests.BaseTest

val RAW_FRAMEWORK_NAME = "Raw SQLite"

open class RawTest(ctx: Context) : BaseTest<Player>(playerCreator = { Player() },
        frameworkName = RAW_FRAMEWORK_NAME) {

    val helper = PlayerDBHelper(ctx)
    val db = helper.writableDatabase

    override fun insert() {
        db.beginTransaction()
        try {
            list.forEach {
                db.insert(TABLE_NAME, null, it.getInsertValues())
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    override fun load() {
        val items = arrayListOf<Player>()
        db.rawQuery("SELECT * FROM $TABLE_NAME", null).use {
            while (it.moveToNext()) {
                items += Player.fromCursor(it)
            }
        }
    }

    override fun delete() {
        db.execSQL("DELETE FROM $TABLE_NAME")
    }
}

class RawTestPerformance2(ctx: Context) : RawTest(ctx) {
    override fun load() {
        val items = arrayListOf<Player>()
        db.rawQuery("SELECT * FROM $TABLE_NAME", null).use {
            while (it.moveToNext()) {
                val player = Player.fromCursor(it)
                items += player
                player.toString()
            }
        }
    }
}
