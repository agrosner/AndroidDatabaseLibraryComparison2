package com.grosner.androiddatabaselibrarycomparison2.tests

inline fun <reified T : IPlayer> randomPlayerList(playerCreator: () -> T) = mutableListOf<T>().apply {
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