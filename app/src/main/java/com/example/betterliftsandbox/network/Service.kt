package com.example.betterliftsandbox.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class NetworkService {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl("https://wger.de/api/v2/")
        .build()

    private val exerciseApiService = retrofit.create(ExerciseApiService::class.java)

    suspend fun fetchExerciseInfoBase(): NetworkExerciseInfoBase = withContext(Dispatchers.IO) {
        exerciseApiService.getExerciseInfoBase()
    }

    suspend fun fetchExerciseImageBase(): NetworkExerciseImageBase = withContext(Dispatchers.IO) {
        exerciseApiService.getExerciseImageBase()
    }

    suspend fun fetchAllExercises(limit: Int): NetworkExerciseContainer {
        return withContext(Dispatchers.IO) {
            exerciseApiService.getAllExercises(limit = limit)
        }
    }

    suspend fun fetchAllExerciseImages(limit: Int): NetworkExerciseImageContainer {
        return withContext(Dispatchers.IO) {
            exerciseApiService.getAllExerciseImages(limit = limit)
        }
    }
}

enum class Category {
    ABS(10),
    ARMS(8),
    BACK(12),
    CALVES(14),
    CARDIO(15),
    CHEST(11),
    LEGS(9),
    SHOULDERS(13),
    MUSCLETYPE;

    var muscleNum: Int? = 0

    constructor()

    constructor(
        muscleNum: Int,
    ){
        this.muscleNum = muscleNum
    }

    fun convertToString(value: Int): String {
        return when(value) {
            ABS.muscleNum -> "Abs"
            ARMS.muscleNum -> "Arms"
            BACK.muscleNum -> "Back"
            CALVES.muscleNum -> "Calves"
            CARDIO.muscleNum -> "Cardio"
            CHEST.muscleNum -> "Chest"
            LEGS.muscleNum -> "Legs"
            SHOULDERS.muscleNum -> "Shoulders"
            else -> ""
        }
    }
}

interface ExerciseApiService {

    @GET("exercise")
    suspend fun getExerciseInfoBase(
        @Query("language") language: Int = 2
    ): NetworkExerciseInfoBase

    @GET("exerciseimage")
    suspend fun getExerciseImageBase(
    ): NetworkExerciseImageBase

    @GET("exercise")
    suspend fun getAllExercises(
        @Query("language") language: Int = 2,
        @Query("limit") limit: Int
    ): NetworkExerciseContainer

    @GET("exerciseimage")
    suspend fun getAllExerciseImages(
        @Query("limit") limit: Int
    ): NetworkExerciseImageContainer

    /*@GET("exercise")
    suspend fun getAbsExercises(
        @Query("language") language: Int = 2,
        @Query("category") category: Int = Category.ABS.id,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int
    ): NetworkExerciseInfoBase

    @GET("exercise")
    suspend fun getArmsExercises(
        @Query("language") language: Int = 2,
        @Query("category") category: Int = Category.ARMS.id
    )

    @GET("exercise")
    suspend fun getBackExercises(
        @Query("language") language: Int = 2,
        @Query("category") category: Int = Category.BACK.id
    )

    @GET("exercise")
    suspend fun getCalvesExercises(
        @Query("language") language: Int = 2,
        @Query("category") category: Int = Category.CALVES.id
    )

    @GET("exercise")
    suspend fun getCardioExercises(
        @Query("language") language: Int = 2,
        @Query("category") category: Int = Category.CARDIO.id
    )

    @GET("exercise")
    suspend fun getChestExercises(
        @Query("language") language: Int = 2,
        @Query("category") category: Int = Category.CHEST.id
    )

    @GET("exercise")
    suspend fun getLegsExercises(
        @Query("language") language: Int = 2,
        @Query("category") category: Int = Category.LEGS.id
    )

    @GET("exercise")
    suspend fun getShouldersExercises(
        @Query("language") language: Int = 2,
        @Query("category") category: Int = Category.SHOULDERS.id
    )*/
}