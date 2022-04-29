package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.asDatabaseModel
import com.udacity.asteroidradar.network.AsteroidsApi
import com.udacity.asteroidradar.network.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import com.udacity.asteroidradar.utils.getCurrentWeekDates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {
    private val dates = getNextSevenDaysFormattedDates()
    private val filterRange = MutableLiveData<List<String>>()

    init {
        val range = listOf<String>(dates[0], dates[0])
        filterRange.value = range
    }

    val pictureOfDay = MutableLiveData<PictureOfDay>()
    val mutableAsteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>> = Transformations.switchMap(filterRange) {
        Transformations.map(
            if (filterRange.value?.size!! > 0)
                database.asteroidDao.getAsteroidsByDate(
                    filterRange.value?.get(0)!!,
                    filterRange.value?.get(1)!!
                ) else database.asteroidDao.getAll()
        ) { it.asDomainModel() }
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val jsonResponse = JSONObject(
                AsteroidsApi.retrofitService.getAsteroids(
                    dates[0],
                    dates[dates.size - 1]
                )
            )
            val asteroidsList = parseAsteroidsJsonResult(jsonResponse)
            database.asteroidDao.insertAll(*asteroidsList.asDatabaseModel())
        }
    }

    fun getTodayAsteroids() {
        filterRange.value = listOf<String>(dates[0], dates[0])
    }

    fun getWeekAsteroids() {
        val currentWeekDays = getCurrentWeekDates()
        filterRange.value =
            listOf<String>(currentWeekDays[0], currentWeekDays[currentWeekDays.size - 1])
    }

    suspend fun getAllAsteroids() {
        filterRange.value = listOf()
    }

    suspend fun getPictureOfDay() {
        withContext(Dispatchers.IO) {
            pictureOfDay.postValue(AsteroidsApi.retrofitService.getPictureOfDay())
        }
    }

    suspend fun deleteOldAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteOldAsteroids()
        }
    }
}