package com.grosner.androiddatabaselibrarycomparison2.requery

import android.content.Context
import com.grosner.androiddatabaselibrarycomparison2.tests.BaseTest
import com.grosner.androiddatabaselibrarycomparison2.tests.printToString
import io.requery.android.sqlite.DatabaseSource
import io.requery.sql.EntityDataStore


/**
 * Description:
 */


val REQUERY_FRAMEWORK_NAME = "Requery"

class RequeryTest(val ctx: Context) : BaseTest<PlayerEntity>(playerCreator = { PlayerEntity() },
        frameworkName = REQUERY_FRAMEWORK_NAME) {

    val source = DatabaseSource(ctx, Models.DEFAULT, 1)
    val store = EntityDataStore<PlayerEntity>(source.configuration)

    override fun init() {
    }

    override fun insert() {
        store.insert(list)
    }

    override fun load() {
        store.select(PlayerEntity::class.java)
                .from(PlayerEntity::class.java)
                .get().toList()
    }

    override fun delete() {
        store.delete().from(PlayerEntity::class.java).get().value()
    }
}

open class RequeryTestPerformance(ctx: Context) : BaseTest<Player2Entity>(
        playerCreator = { Player2Entity() },
        frameworkName = REQUERY_FRAMEWORK_NAME) {

    val source = DatabaseSource(ctx, Models.DEFAULT, 1)
    val store = EntityDataStore<Player2Entity>(source.configuration)

    override fun init() {
    }

    override fun insert() {
        store.insert(list)
    }

    override fun load() {
        store.select(Player2Entity::class.java)
                .from(Player2Entity::class.java)
                .get().toList()
    }

    override fun delete() {
        store.delete().from(Player2Entity::class.java).get().value()
    }
}

class RequeryTestPerformance2(ctx: Context) : RequeryTestPerformance(ctx) {
    override fun load() {
        store.select(Player2Entity::class.java)
                .from(Player2Entity::class.java)
                .get().forEach { it.printToString }
    }
}