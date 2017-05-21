package com.grosner.androiddatabaselibrarycomparison2.tests

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction


data class Result(val frameworkName: String, val insertTime: Long, val loadTime: Long) {
    val totalTime
        get() = insertTime + loadTime

}

val currentTime
    get() = System.currentTimeMillis()

inline fun <reified T : Any> Collection<T>.fastDelete() = FastStoreModelTransaction.deleteBuilder(modelAdapter<T>()).addAll(this)
