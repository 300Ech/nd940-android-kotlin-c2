package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM databaseasteroid ORDER BY closeApproachDate")
    fun getAll(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate >= :fromDate AND closeApproachDate <= :toDate ORDER BY closeApproachDate")
    fun getAsteroidsByDate(fromDate: String, toDate: String): LiveData<List<DatabaseAsteroid>>

    @Query("DELETE FROM databaseasteroid WHERE closeApproachDate < date('now')")
    fun deleteOldAsteroids()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids"
            ).build()
        }
    }

    return INSTANCE
}