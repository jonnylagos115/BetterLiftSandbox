package com.example.betterliftsandbox.database

import androidx.room.*
import com.example.betterliftsandbox.domain.Exercise
import com.google.gson.Gson


/**
 * Database entities go in this file. These are responsible for reading and writing from the
 * database.
 */

/**
 * DatabaseExercises represents a exercise entity in the database.
 */
@Entity
data class DatabaseExercise constructor(
    @PrimaryKey val exerciseId: Int = 0,
    val exerciseName: String,
    val muscleGroupName: String,
    val description: String,
    var imageResUrl: String? = null)

@Entity(tableName = "db_network_request_records")
data class DatabaseNetworkRequestRecords constructor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val offset: Int
)


data class ExerciseList(
    val exerciseList: MutableList<Exercise> = mutableListOf<Exercise>()
)

class ExerciseListTypeConverter {
    @TypeConverter
    fun toExerciseList(value: String): ExerciseList {
        return Gson().fromJson(value, ExerciseList::class.java)
    }

    @TypeConverter
    fun fromExerciseList(value: ExerciseList): String {
        return Gson().toJson(value)
    }
}

/**
 * Map DatabaseExercises to domain entities
 */
fun List<DatabaseExercise>.asDomainModel(): List<Exercise> {
    return map {
        Exercise(
            exerciseId = it.exerciseId,
            exerciseName = it.exerciseName,
            muscleGroupName = it.muscleGroupName,
            description = it.description,
            imageResUrl = it.imageResUrl
        )
    }
}

fun DatabaseExercise.asDomainModel(): Exercise {
    return let {
        Exercise(
            exerciseId = it.exerciseId,
            exerciseName = it.exerciseName,
            muscleGroupName = it.muscleGroupName,
            description = it.description,
            imageResUrl = it.imageResUrl
        )
    }
}

@JvmName("asDatabaseModelExercise")
fun List<Exercise>.asDatabaseModel(): List<DatabaseExercise> {
    return map {
        DatabaseExercise(
            exerciseId = it.exerciseId,
            exerciseName = it.exerciseName,
            muscleGroupName = it.muscleGroupName,
            description = it.description,
            imageResUrl = it.imageResUrl)
    }
}

fun Exercise.asDatabaseModel(): DatabaseExercise {
    return let {
        DatabaseExercise(
            exerciseId = it.exerciseId,
            exerciseName = it.exerciseName,
            muscleGroupName = it.muscleGroupName,
            description = it.description,
            imageResUrl = it.imageResUrl)
    }
}