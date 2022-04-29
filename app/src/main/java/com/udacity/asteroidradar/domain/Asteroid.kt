package com.udacity.asteroidradar.domain

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.database.DatabaseAsteroid
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Asteroid(
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable {
    fun isPotentiallyHazardousToText(): String {
        return if (this.isPotentiallyHazardous)
            "The asteroid ${this.codename} is potentially hazardous"
        else "The asteroid ${this.codename} is not hazardous"
    }
}


fun List<Asteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}