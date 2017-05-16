package com.grosner.androiddatabaselibrarycomparison2.greendao

import android.content.Context
import com.grosner.androiddatabaselibrarycomparison2.tests.BaseTest
import com.grosner.androiddatabaselibrarycomparison2.tests.printToString

val GREENDAO_FRAMEWORK_NAME = "GreenDao"

open class GreenDaoTest(ctx: Context) : BaseTest<Player>(playerCreator = { Player() },
        frameworkName = GREENDAO_FRAMEWORK_NAME) {

    val helper = DaoMaster.DevOpenHelper(ctx, "greendao-db")
    val db = helper.writableDb!!
    val daoSession = DaoMaster(db).newSession()!!
    val playerDao = daoSession.playerDao!!

    override fun insert() {
        playerDao.insertInTx(list)
    }

    override fun load() {
        playerDao.loadAll()
    }

    override fun delete() {
        playerDao.deleteAll()
    }
}

class GreenDaoTestPerformance2(ctx: Context) : GreenDaoTest(ctx) {
    override fun load() {
        playerDao.queryBuilder().listLazy().forEach { it.printToString }
    }
}