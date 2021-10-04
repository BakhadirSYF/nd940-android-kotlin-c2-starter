package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Apod
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch


/**
 * MainViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during activity
 * or fragment lifecycle events.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "MainViewModel"

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    // The internal MutableLiveData to store asteroid data and to handle navigation to the
    // selected asteroid list item
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the navigation item
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    // The internal MutableLiveData to store Astronomy Picture Of the Day data (apod)
    private val _apod = MutableLiveData<Apod>()

    // The external immutable LiveData for the apod data
    val apod: LiveData<Apod>
        get() = _apod

    // The internal MutableLiveData to store boolean value that changes when list of asteroids
    // displayed to the user. Used for progress bar visibility
    private val _asteroidsDisplayed = MutableLiveData<Boolean>()

    // The external immutable LiveData used for progress bar visibility via BindingsAdapters
    val asteroidsDisplayed: LiveData<Boolean>
        get() = _asteroidsDisplayed

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        viewModelScope.launch {
            _asteroidsDisplayed.value = false
            try {
                asteroidsRepository.refreshAsteroids()
                _apod.value = asteroidsRepository.getApod()
            } catch (e: Exception) {
                Log.d(TAG, e.printStackTrace().toString())
            }

        }
    }

    /**
     * When the Asteroid list item clicked, set the [_navigateToSelectedAsteroid] [MutableLiveData]
     * @param asteroid The [Asteroid] that was clicked on.
     */
    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedAsteroid is set to null
     */
    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    /**
     * After asteroid list displayed to the user, set [_asteroidsDisplayed] to true
     */
    fun displayAsteroidListComplete() {
        _asteroidsDisplayed.value = true
    }

    /**
     * Immutable LiveData list of asteroids. Observed from [MainFragment] for updating list adapter
     */
    val asteroidList = asteroidsRepository.asteroids

    /**
     * Factory for constructing MainViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}