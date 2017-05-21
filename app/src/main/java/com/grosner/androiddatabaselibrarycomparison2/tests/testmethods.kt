package com.grosner.androiddatabaselibrarycomparison2.tests

inline fun <T : IPlayer> randomPlayerList(playerCreator: () -> T) = mutableListOf<T>().apply {
    val ageRandom = java.util.Random(System.currentTimeMillis())
    (0..MainActivity.Companion.LOOP_COUNT).forEach { index ->
        add(playerCreator().apply {
            id = index.toString()
            firstName = "Andrew"
            lastName = "Grosner"
            age = ageRandom.nextInt()
            position = "Pitcher"
        })
    }
}

abstract class BaseTest<P : IPlayer>(val playerCreator: () -> P,
                                     val frameworkName: String) {

    val list = randomPlayerList(playerCreator)

    fun test(): Result {
        init()
        delete()

        var insertTime = currentTime
        insert()
        insertTime = currentTime - insertTime

        var loadTime = currentTime
        load()
        loadTime = currentTime - loadTime

        delete()
        dispose()
        return Result(frameworkName, insertTime, loadTime)
    }

    open fun init() = Unit

    abstract fun insert()

    abstract fun load()

    abstract fun delete()

    abstract fun dispose()
}