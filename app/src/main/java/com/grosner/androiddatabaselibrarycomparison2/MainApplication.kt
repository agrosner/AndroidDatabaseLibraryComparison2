package com.grosner.androiddatabaselibrarycomparison2

import android.app.Application
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import io.realm.Realm

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FlowManager.init(FlowConfig.Builder(this).build())
        Realm.init(this)
    }
}