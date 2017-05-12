package com.grosner.androiddatabaselibrarycomparison2.tests

import android.app.Application
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import io.realm.Realm

class MainApplication : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        com.raizlabs.android.dbflow.config.FlowManager.init(com.raizlabs.android.dbflow.config.FlowConfig.Builder(this).build())
        io.realm.Realm.init(this)
    }
}