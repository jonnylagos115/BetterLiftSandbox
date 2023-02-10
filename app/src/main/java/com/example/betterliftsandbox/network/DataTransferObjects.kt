package com.example.betterliftsandbox.network

import com.example.betterliftsandbox.database.DatabaseExercise
import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 *
 * @see domain package for that
 */

/**
 *
 * Network response for Exercise object on first level (API endpoint: /api/v2/exerciseinfo/)
 *
 * {
 *   "count": 488,
 *   "next": "https://wger.de/api/v2/exerciseinfo/?limit=20&offset=20",
 *   "previous": null,
 *   "results": []
 * }
 *
 * Network response for Exercise object on second level (API endpoint: /api/v2/exerciseinfo/)
 * "results": [
 *   {
 *      "id": 345,
 *      "name": "2 Handed Kettlebell Swing",
 *      "description": "Two Handed Russian Style Kettlebell swing"
 *   }
 *]
 *
 * Network response for Exercise muscle groups on first level (API endpoint: /api/v2/exercisecategory/)
 * {
 *   "count": 8,
 *   "next": null,
 *   "previous": null,
 *   "results": []
 * }
 *
 * Network response for Exercise muscle groups on second level (API endpoint: /api/v2/exercisecategory/)
 * "results": [
 *   {
 *      "id": 10,
 *      "name": "Abs"
 *   }
 *]
 *
 * Network response for Exercise language on first level (API endpoint: /api/v2/language/)
 * {
 *   "count": 24,
 *   "next": "https://wger.de/api/v2/language/?limit=20&offset=20",
 *   "previous": null,
 *   "results": []
 * }
 *
 * Network response for Exercise language on second level (API endpoint: /api/v2/language/)
 * "results": [
 *   {
 *      "id": 2,
 *      "short_name": "en"
 *      "full_name": "English"
 *   }
 *]
 */

@JsonClass(generateAdapter = true)
data class NetworkExerciseContainer(
    @Json(name = "results") val exercises: List<NetworkExerciseInfo>
)

@JsonClass(generateAdapter = true)
data class NetworkExerciseImageContainer(
    @Json(name = "results") val exerciseImages: List<NetworkExerciseImage>
)

// First level
@JsonClass(generateAdapter = true)
data class NetworkExerciseInfoBase(
    val count: Int,
    val next: String?,
    val previous: String?
)

@JsonClass(generateAdapter = true)
data class NetworkExerciseInfo(
    @Json(name = "exercise_base") val id: Int,
    @Json(name = "name") val exerciseName: String,
    val description: String,
    val category: Int,
    var imageResUrl: String?
)

@JsonClass(generateAdapter = true)
data class NetworkExerciseImageBase(
    val count: Int,
    val next: String?,
    val previous: String?
)

@JsonClass(generateAdapter = true)
data class NetworkExerciseImage(
    @Json(name = "exercise_base") val id: Int,
    @Json(name = "image") val imageResUrl: String?
)

/**
 * Convert Network results to domain objects
 */
/*fun NetworkExerciseContainer.asDomainModel(): List<Exercise> {
    return exercises.map {
        Exercise(

            muscleGroupName = it.muscle,
            exerciseName = it.name)
    }
}*/

/**
 * Convert Network results to database objects
 */
fun NetworkExerciseContainer.asDatabaseModel(): List<DatabaseExercise> {
    return exercises.map {
        DatabaseExercise(
            exerciseId = it.id,
            exerciseName = it.exerciseName,
            muscleGroupName = Category.MUSCLETYPE.convertToString(it.category),
            description = it.description)
    }
}