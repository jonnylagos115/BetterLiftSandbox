package com.example.betterliftsandbox.domain

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 * @see network for objects that parse or prepare network calls
 */

data class Exercise(
    val exerciseId: Int,
    val exerciseName: String,
    val muscleGroupName: String,
    val description: String,
    val imageResUrl: String?
) {
    var isSelected: Boolean? = false
    val numberOfPounds: MutableList<Int> = mutableListOf()
    val numberOfReps: MutableList<Int> = mutableListOf()
    var numberOfSets: Int = 1
}
