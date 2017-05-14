package com.grosner.androiddatabaselibrarycomparison2.raw

import android.content.ContentValues
import android.database.Cursor
import com.grosner.androiddatabaselibrarycomparison2.raw.PlayerContract.PlayerEntry.COLUMN_NAME_AGE
import com.grosner.androiddatabaselibrarycomparison2.raw.PlayerContract.PlayerEntry.COLUMN_NAME_FIRST_NAME
import com.grosner.androiddatabaselibrarycomparison2.raw.PlayerContract.PlayerEntry.COLUMN_NAME_ID
import com.grosner.androiddatabaselibrarycomparison2.raw.PlayerContract.PlayerEntry.COLUMN_NAME_LAST_NAME
import com.grosner.androiddatabaselibrarycomparison2.raw.PlayerContract.PlayerEntry.COLUMN_NAME_POSITION
import com.grosner.androiddatabaselibrarycomparison2.tests.IPlayer
import com.raizlabs.android.dbflow.kotlinextensions.set

object PlayerContract {

    object PlayerEntry {
        const val TABLE_NAME = "Player"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_FIRST_NAME = "firstName"
        const val COLUMN_NAME_LAST_NAME = "lastName"
        const val COLUMN_NAME_AGE = "age"
        const val COLUMN_NAME_POSITION = "position"
    }

    const val SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS ${PlayerEntry.TABLE_NAME}(" +
            "${PlayerEntry.COLUMN_NAME_ID} TEXT, " +
            "${PlayerEntry.COLUMN_NAME_FIRST_NAME} TEXT, " +
            "${PlayerEntry.COLUMN_NAME_LAST_NAME} TEXT, " +
            "${PlayerEntry.COLUMN_NAME_AGE} INTEGER, " +
            "${PlayerEntry.COLUMN_NAME_POSITION} TEXT, " +
            "PRIMARY KEY(${PlayerEntry.COLUMN_NAME_ID}))"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${PlayerEntry.TABLE_NAME}"
}

class Player : IPlayer {

    override var id = ""
    override var firstName = ""
    override var lastName = ""
    override var age = 0
    override var position = ""

    fun getInsertValues() = ContentValues().apply {
        this[COLUMN_NAME_ID] = id
        this[COLUMN_NAME_FIRST_NAME] = firstName
        this[COLUMN_NAME_LAST_NAME] = lastName
        this[COLUMN_NAME_AGE] = age
        this[COLUMN_NAME_POSITION] = position
    }

    companion object {

        fun fromCursor(cursor: Cursor) = Player().apply {
            id = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID))
            firstName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FIRST_NAME))
            lastName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LAST_NAME))
            age = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID))
            position = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_POSITION))
        }
    }
}