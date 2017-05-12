package com.grosner.androiddatabaselibrarycomparison2.dbflow

import com.grosner.androiddatabaselibrarycomparison2.tests.IPlayer
import com.grosner.androiddatabaselibrarycomparison2.tests.MainActivity
import com.raizlabs.android.dbflow.annotation.Database
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

@Database(version = DBFlowDatabase.VERSION)
object DBFlowDatabase {
    const val VERSION = 1
}

@Table(database = DBFlowDatabase::class, allFields = true)
class Player : IPlayer, BaseModel() {

    @PrimaryKey
    override var id = ""

    override var firstName = ""

    override var lastName = ""

    override var age = 0

    override var position = ""

}

@Table(database = DBFlowDatabase::class, allFields = true, orderedCursorLookUp = true,
        assignDefaultValuesFromCursor = false, cachingEnabled = true, cacheSize = MainActivity.LOOP_COUNT)
class Player2 : IPlayer, BaseModel() {

    @PrimaryKey
    override var id = ""

    override var firstName = ""

    override var lastName = ""

    override var age = 0

    override var position = ""

}