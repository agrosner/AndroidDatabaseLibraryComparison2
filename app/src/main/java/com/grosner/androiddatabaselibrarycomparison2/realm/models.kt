package com.grosner.androiddatabaselibrarycomparison2.realm

import com.grosner.androiddatabaselibrarycomparison2.tests.IPlayer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import io.realm.RealmObject

open class Player : IPlayer, RealmObject() {

    @PrimaryKey
    override var id = ""

    override var firstName = ""

    override var lastName = ""

    override var age = 0

    override var position = ""
}