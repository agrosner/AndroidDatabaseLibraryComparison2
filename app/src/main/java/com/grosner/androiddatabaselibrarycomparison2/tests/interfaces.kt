package com.grosner.androiddatabaselibrarycomparison2.tests

interface IPlayer {

    var id: String

    var firstName: String

    var lastName: String

    var age: Int

    var position: String

}

val IPlayer.printToString
    get() = "Player2(id='$id', firstName='$firstName', lastName='$lastName', age=$age, position='$position')"