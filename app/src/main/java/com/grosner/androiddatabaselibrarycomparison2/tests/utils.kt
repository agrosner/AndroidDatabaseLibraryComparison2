package com.grosner.androiddatabaselibrarycomparison2.tests


data class Result(val frameworkName: String, val insertStartTime: Long, val loadStartTime: Long)

val currentTime
    get() = System.currentTimeMillis()