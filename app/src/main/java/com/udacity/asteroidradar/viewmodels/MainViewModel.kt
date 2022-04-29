package com.udacity.asteroidradar.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainViewModel(application: Application) : ViewModel() {
    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    val asteroids = asteroidsRepository.asteroids
    val pictureOfDay = asteroidsRepository.pictureOfDay
    private val _isLoading = MutableLiveData<Boolean>()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    init {
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)
                asteroidsRepository.refreshAsteroids()
                asteroidsRepository.getPictureOfDay()
                showTodayAsteroids()
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    val progressBarVisible = Transformations.map(_isLoading) {
        if (it == true) View.VISIBLE else View.GONE
    }

    fun showWeekAsteroids() {
        viewModelScope.launch {
            asteroidsRepository.getWeekAsteroids()
        }
    }

    fun showTodayAsteroids() {
        viewModelScope.launch {
            asteroidsRepository.getTodayAsteroids()
        }
    }

    fun showAllAsteroids() {
        viewModelScope.launch {
            asteroidsRepository.getAllAsteroids()
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct MainViewModel")
        }
    }
}