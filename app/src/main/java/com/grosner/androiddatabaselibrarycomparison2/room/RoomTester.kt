package com.grosner.androiddatabaselibrarycomparison2.room

import android.arch.persistence.room.Room
import android.content.Context
import com.grosner.androiddatabaselibrarycomparison2.tests.BaseTest

const val ROOM_FRAMEWORK_NAME = "room";

class RoomTest(val ctx: Context) : BaseTest<Player>(playerCreator = { Player() }, frameworkName = ROOM_FRAMEWORK_NAME) {

    lateinit var appDatabase: AppDatabase

    lateinit var playerDao: PlayerDao

    override fun init() {
        appDatabase = Room.databaseBuilder(ctx,
                AppDatabase::class.java, "room-db").build()
        playerDao = appDatabase.playerDao();
    }

    override fun insert() {
        playerDao.insertPlayers(list)
    }

    override fun load() {
        playerDao.loadPlayers()
    }

    override fun delete() {
        playerDao.deletePlayers(list)
    }

    override fun dispose() {
        appDatabase.close()
    }
}