package com.grosner.androiddatabaselibrarycomparison2.greendao

import android.content.Context
import com.grosner.androiddatabaselibrarycomparison2.events.LogTestDataEvent
import com.grosner.androiddatabaselibrarycomparison2.tests.MainActivity
import com.grosner.androiddatabaselibrarycomparison2.tests.printToString
import com.grosner.androiddatabaselibrarycomparison2.tests.randomPlayerList
import org.greenrobot.eventbus.EventBus

val GREENDAO_FRAMEWORK_NAME = "GreenDao"

fun testGreenDao(ctx: Context) {

    val helper = DaoMaster.DevOpenHelper(ctx, "greendao-db")
    val db = helper.writableDb
    val daoSession = DaoMaster(db).newSession()

    val playerDao = daoSession.playerDao
    playerDao.deleteAll()

    val list = randomPlayerList { Player() }

    var startTime = System.currentTimeMillis()
    playerDao.insertInTx(list)
    EventBus.getDefault().post(LogTestDataEvent(startTime, GREENDAO_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    playerDao.loadAll()
    EventBus.getDefault().post(LogTestDataEvent(startTime, GREENDAO_FRAMEWORK_NAME, MainActivity.LOAD_TIME))

    playerDao.deleteAll()
}

fun testGreenDaoPerformance2(ctx: Context) {

    val helper = DaoMaster.DevOpenHelper(ctx, "greendao-db")
    val db = helper.writableDb
    val daoSession = DaoMaster(db).newSession()

    val playerDao = daoSession.playerDao
    playerDao.deleteAll()

    val list = randomPlayerList { Player() }

    var startTime = System.currentTimeMillis()
    playerDao.insertInTx(list)
    EventBus.getDefault().post(LogTestDataEvent(startTime, GREENDAO_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    playerDao.queryBuilder().listLazy().forEach { it.printToString }
    EventBus.getDefault().post(LogTestDataEvent(startTime, GREENDAO_FRAMEWORK_NAME, MainActivity.LOAD_TIME))

    playerDao.deleteAll()
}