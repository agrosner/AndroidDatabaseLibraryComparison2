package com.grosner.androiddatabaselibrarycomparison2.room

import android.arch.persistence.room.*
import com.grosner.androiddatabaselibrarycomparison2.tests.IPlayer


@Entity
class Player : IPlayer {

    @PrimaryKey
    override var id = ""

    override var firstName = ""

    override var lastName = ""

    override var age = 0

    override var position = ""
}

@Dao
interface PlayerDao {

    @Query("SELECT * from player")
    fun loadPlayers(): List<Player>

    @Insert
    fun insertPlayers(playerList: List<Player>)

    @Delete
    fun deletePlayers(playerList: List<Player>)
}

@Database(entities = arrayOf(Player::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
}