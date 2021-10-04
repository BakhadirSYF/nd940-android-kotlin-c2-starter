package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Apod
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.network.NetworkAsteroidsContainer
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    private val nextSevenDaysFormattedDates: ArrayList<String> = getNextSevenDaysFormattedDates()

    // Today and seven days from today formatted dates
    // Used for fetching asteroids from database from today onwards
    // and for fetching asteroids from NASA API in the range from today to next seven days
    private val todayFormattedDate = nextSevenDaysFormattedDates[0]
    private val eowFormattedDate = nextSevenDaysFormattedDates[nextSevenDaysFormattedDates.size - 1]

    /**
     * A list of asteroids that can be shown on the screen.
     */

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidsDao.getAsteroids(todayFormattedDate)) {
            it.asDomainModel()
        }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the list of asteroids, observe [asteroids]
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {

            val jsonResponse = NasaApi.retrofitService.getAsteroidsAsync(
                mapOf(
                    "start_date" to todayFormattedDate,
                    "end_date" to eowFormattedDate
                )
            ).await()

            val asteroidList =
                NetworkAsteroidsContainer(parseAsteroidsJsonResult(JSONObject(jsonResponse)))

            database.asteroidsDao.insertAll(*asteroidList.asDatabaseModel())
        }
    }

    suspend fun getApod(): Apod {
        return NasaApi.retrofitService.getApod(todayFormattedDate)
    }

}