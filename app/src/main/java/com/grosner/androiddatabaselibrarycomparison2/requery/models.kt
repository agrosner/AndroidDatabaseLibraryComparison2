package com.grosner.androiddatabaselibrarycomparison2.requery

import com.grosner.androiddatabaselibrarycomparison2.tests.IPlayer
import io.requery.Entity
import io.requery.Key

/**
 * Description:
 */
@Entity
interface Player : IPlayer {

    @get:Key
    override var id: String
    override var firstName: String
    override var lastName: String
    override var age: Int
    override var position: String
}