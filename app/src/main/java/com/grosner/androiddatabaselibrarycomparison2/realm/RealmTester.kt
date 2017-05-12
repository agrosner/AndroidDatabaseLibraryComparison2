package com.grosner.androiddatabaselibrarycomparison2.realm

import com.grosner.androiddatabaselibrarycomparison2.MainActivity
import com.grosner.androiddatabaselibrarycomparison2.events.LogTestDataEvent
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import java.util.*

val REALM_FRAMEWORK_NAME = "Realm"

fun testRealmModels() {


    val realm = Realm.getDefaultInstance()
    realm.executeTransaction { it.where(Player::class.java).findAll().deleteAllFromRealm() }

    val ageRandom = Random(System.currentTimeMillis())
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
    realm.executeTransaction {
        it.copyToRealmOrUpdate(list)
    }
    EventBus.getDefault().post(LogTestDataEvent(startTime, REALM_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    realm.where(Player::class.java).findAll()
    EventBus.getDefault().post(LogTestDataEvent(startTime, REALM_FRAMEWORK_NAME, MainActivity.LOAD_TIME))
}