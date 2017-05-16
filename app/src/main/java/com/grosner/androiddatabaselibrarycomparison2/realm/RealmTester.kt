package com.grosner.androiddatabaselibrarycomparison2.realm

import com.grosner.androiddatabaselibrarycomparison2.tests.BaseTest
import com.grosner.androiddatabaselibrarycomparison2.tests.printToString
import io.realm.Realm

val REALM_FRAMEWORK_NAME = "Realm"

open class RealmDefault : BaseTest<Player>(playerCreator = { Player() },
        frameworkName = REALM_FRAMEWORK_NAME) {

    lateinit var realm: Realm

    override fun init() {
        realm = Realm.getDefaultInstance()
    }

    override fun insert() {
        realm.executeTransaction {
            it.copyToRealmOrUpdate(list)
        }
    }

    override fun load() {
        realm.where(Player::class.java).findAll()
    }

    override fun delete() {
        realm.executeTransaction {
            realm.deleteAll()
        }
    }
}

class RealmPerformance : RealmDefault() {
    override fun load() {
        realm.where(Player::class.java).findAll().forEach {
            it.printToString
        }
    }
}