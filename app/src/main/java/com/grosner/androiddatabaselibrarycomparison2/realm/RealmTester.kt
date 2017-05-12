package com.grosner.androiddatabaselibrarycomparison2.realm

import com.grosner.androiddatabaselibrarycomparison2.events.LogTestDataEvent
import com.grosner.androiddatabaselibrarycomparison2.tests.MainActivity
import com.grosner.androiddatabaselibrarycomparison2.tests.printToString
import com.grosner.androiddatabaselibrarycomparison2.tests.randomPlayerList
import io.realm.Realm
import org.greenrobot.eventbus.EventBus

val REALM_FRAMEWORK_NAME = "Realm"

fun testRealmModels() {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction { it.where(Player::class.java).findAll().deleteAllFromRealm() }

    val list = randomPlayerList { Player() }

    var startTime = System.currentTimeMillis()
    realm.executeTransaction {
        it.copyToRealmOrUpdate(list)
    }
    EventBus.getDefault().post(LogTestDataEvent(startTime, REALM_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    realm.where(Player::class.java).findAll()
    EventBus.getDefault().post(LogTestDataEvent(startTime, REALM_FRAMEWORK_NAME, MainActivity.LOAD_TIME))
}

fun testRealmModelsPerformance2() {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction { it.where(Player::class.java).findAll().deleteAllFromRealm() }

    val list = randomPlayerList { Player() }

    var startTime = System.currentTimeMillis()
    realm.executeTransaction {
        it.copyToRealmOrUpdate(list)
    }
    EventBus.getDefault().post(LogTestDataEvent(startTime, REALM_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    realm.where(Player::class.java).findAll().forEach {
        it.printToString
    }
    EventBus.getDefault().post(LogTestDataEvent(startTime, REALM_FRAMEWORK_NAME, MainActivity.LOAD_TIME))
}