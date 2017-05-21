package com.grosner.androiddatabaselibrarycomparison2.greendao

import android.content.Context
import com.grosner.androiddatabaselibrarycomparison2.tests.BaseTest
import com.grosner.androiddatabaselibrarycomparison2.tests.printToString
import org.greenrobot.greendao.database.Database

val GREENDAO_FRAMEWORK_NAME = "GreenDao"

open class GreenDaoTest(val ctx: Context) : BaseTest<Player>(playerCreator = { Player() },
        frameworkName = GREENDAO_FRAMEWORK_NAME) {

    lateinit var helper: DaoMaster.DevOpenHelper
    lateinit var db: Database
    lateinit var daoSession: DaoSession
    lateinit var playerDao: PlayerDao

    override fun init() {
        helper = DaoMaster.DevOpenHelper(ctx, "greendao-db")
        db = helper.writableDb!!
        daoSession = DaoMaster(db).newSession()
        playerDao = daoSession.playerDao!!
    }

    override fun insert() {
        playerDao.insertInTx(list)
    }

    override fun load() {
        playerDao.loadAll()
    }

    override fun delete() {
        playerDao.deleteAll()
    }

    override fun dispose() {
        db.close()
    }
}

class GreenDaoTestPerformance2(ctx: Context) : GreenDaoTest(ctx) {
    override fun load() {
        playerDao.queryBuilder().listLazy().forEach { it.printToString }
    }
}