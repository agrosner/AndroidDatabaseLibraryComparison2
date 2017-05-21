package com.grosner.androiddatabaselibrarycomparison2.tests

import com.raizlabs.android.dbflow.config.FlowManager
import io.realm.Realm

class MainApplication : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        FlowManager.init(this)
        Realm.init(this)
    }
}