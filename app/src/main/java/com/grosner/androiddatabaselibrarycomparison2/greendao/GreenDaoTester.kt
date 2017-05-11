package com.grosner.androiddatabaselibrarycomparison2.greendao

import android.content.Context
import com.grosner.androiddatabaselibrarycomparison2.MainActivity
import com.grosner.androiddatabaselibrarycomparison2.events.LogTestDataEvent
import org.greenrobot.eventbus.EventBus
import java.util.*

val GREENDAO_FRAMEWORK_NAME = "GreenDao"

fun testGreenDao(ctx: Context) {

    val helper = DaoMaster.DevOpenHelper(ctx, "greendao-db")
    val db = helper.writableDb
    val daoSession = DaoMaster(db).newSession()

    val playerDao = daoSession.playerDao
    playerDao.deleteAll()

    var ageRandom = Random(System.currentTimeMillis())
    val list = mutableListOf<Player>()
    (0..MainActivity.LOOP_COUNT).forEach {
        list += Player().apply {
            id = it.toString()
            firstName = "Andrew"
            lastName = "Grosner"
            age = ageRandom.nextInt()
            position = "Pitcher"
        }
    }

    var startTime = System.currentTimeMillis()
    playerDao.insertInTx(list)
    EventBus.getDefault().post(LogTestDataEvent(startTime, GREENDAO_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    playerDao.loadAll()
    EventBus.getDefault().post(LogTestDataEvent(startTime, GREENDAO_FRAMEWORK_NAME, MainActivity.LOAD_TIME))

    playerDao.deleteAll()
}