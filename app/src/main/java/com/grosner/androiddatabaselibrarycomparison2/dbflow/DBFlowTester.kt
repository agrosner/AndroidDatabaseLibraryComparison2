package com.grosner.androiddatabaselibrarycomparison2.dbflow

import com.grosner.androiddatabaselibrarycomparison2.tests.*
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.sql.language.Delete

val DBFLOW_FRAMEWORK_NAME = "DBFlow"

open class DBFlowTest : BaseTest<Player>(playerCreator = { Player() },
        frameworkName = DBFLOW_FRAMEWORK_NAME) {
    override fun init() {
    }

    override fun insert() {
        databaseForTable<Player>().executeTransaction {
            modelAdapter<Player>().insertAll(list)
        }
    }

    override fun load() {
        (select from Player::class).list
    }

    override fun delete() {
        delete<Player>().execute()
    }

    override fun dispose() {
        database<DBFlowDatabase>().destroy(FlowManager.getContext())
    }
}

open class DBFlowTestPerformance : BaseTest<Player2>(playerCreator = { Player2() },
        frameworkName = DBFLOW_FRAMEWORK_NAME) {
    override fun init() {
        // preload these to cut down on time loaded.
        modelAdapter<Player>().apply {
            listModelSaver
            listModelLoader
        }
    }


    override fun insert() {
        databaseForTable<Player2>().executeTransaction {
            modelAdapter<Player2>().insertAll(list)
        }
    }

    override fun load() {
        (select from Player2::class).list
    }

    override fun delete() {
        delete<Player2>().execute()
    }

    override fun dispose() {
        database<DBFlowDatabase>().destroy(FlowManager.getContext())
    }
}


class DBFlowTestPerformance2 : DBFlowTestPerformance() {
    override fun load() {
        (select from Player2::class).cursorResult.use {
            it.iterator().forEach {
                it.printToString
            }
        }
    }
}