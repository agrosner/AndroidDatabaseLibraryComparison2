package com.grosner.androiddatabaselibrarycomparison2.requery

import android.content.Context
import com.grosner.androiddatabaselibrarycomparison2.tests.MainActivity
import com.grosner.androiddatabaselibrarycomparison2.events.LogTestDataEvent
import com.grosner.androiddatabaselibrarycomparison2.tests.randomPlayerList
import io.requery.android.sqlite.DatabaseSource
import io.requery.sql.EntityDataStore
import org.greenrobot.eventbus.EventBus


/**
 * Description:
 */


val REQUERY_FRAMEWORK_NAME = "Requery"

fun testRequery(ctx: Context) {

    val source = DatabaseSource(ctx, Models.DEFAULT, 1)
    val store = EntityDataStore<PlayerEntity>(source.configuration)
    store.delete().from(PlayerEntity::class.java).get().value()

    val list = randomPlayerList { PlayerEntity() }

    var startTime = System.currentTimeMillis()
    store.insert(list)
    EventBus.getDefault().post(LogTestDataEvent(startTime, REQUERY_FRAMEWORK_NAME, MainActivity.SAVE_TIME))

    startTime = System.currentTimeMillis()
    store.select(PlayerEntity::class.java)
            .from(PlayerEntity::class.java)
            .get().toList()
    EventBus.getDefault().post(LogTestDataEvent(startTime, REQUERY_FRAMEWORK_NAME, MainActivity.LOAD_TIME))

    store.delete().from(PlayerEntity::class.java).get().value()
}