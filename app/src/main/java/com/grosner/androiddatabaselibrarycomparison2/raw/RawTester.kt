package com.grosner.androiddatabaselibrarycomparison2.raw

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.grosner.androiddatabaselibrarycomparison2.raw.PlayerContract.PlayerEntry.TABLE_NAME
import com.grosner.androiddatabaselibrarycomparison2.tests.BaseTest

val RAW_FRAMEWORK_NAME = "Raw SQLite"

open class RawTest(val ctx: Context) : BaseTest<Player>(playerCreator = { Player() },
        frameworkName = RAW_FRAMEWORK_NAME) {

    lateinit var helper: PlayerDBHelper
    lateinit var db: SQLiteDatabase

    override fun init() {
        helper = PlayerDBHelper(ctx)
        db = helper.writableDatabase
    }

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

    override fun dispose() {
        db.close()
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
