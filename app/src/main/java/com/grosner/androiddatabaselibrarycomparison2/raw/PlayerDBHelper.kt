package com.grosner.androiddatabaselibrarycomparison2.raw

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.grosner.androiddatabaselibrarycomparison2.raw.PlayerContract.SQL_CREATE_ENTRIES
import com.grosner.androiddatabaselibrarycomparison2.raw.PlayerContract.SQL_DELETE_ENTRIES

class PlayerDBHelper(context: Context) : SQLiteOpenHelper(context, "RawDB.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}