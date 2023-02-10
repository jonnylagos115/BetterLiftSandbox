package com.example.betterliftsandbox.repository

import android.net.Network
import com.example.betterliftsandbox.database.*
import com.example.betterliftsandbox.domain.Exercise
import com.example.betterliftsandbox.network.NetworkExerciseImage
import com.example.betterliftsandbox.network.NetworkService
import com.example.betterliftsandbox.network.asDatabaseModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

// Classes in the data and business layer expose
// either suspend functions or Flows

private const val OFFSET_REQUEST_MAX = 20
private const val LIMIT_SET = 20
class ExerciseRepo private constructor(
    private val exerciseDao: ExerciseDao,
    private val networkRequestRecordsDao: DBNetworkRequestRecords,
    private val exerciseService: NetworkService,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val TAG = "ExerciseRepo"
    /**
     * Returns true if we should make a network request.
     * Queries for last entry row by, if id is invalid it will return null, which
     * indicates that database is empty, returning true so that to populate database with get() network call
     * for all exercises
     */


    val exerciseStream: Flow<List<Exercise>>
        get() = exerciseDao.getLiveExercises()
            .map{ it.asDomainModel() }

    //fun exercisePagingSource() = ExercisePagingSource(this)

    suspend fun fetchAllNetworkExercises(){
        withContext(Dispatchers.IO){
            val fetchedExerciseInfoBase = exerciseService.fetchExerciseInfoBase()
            val fetchedExerciseImageBase = exerciseService.fetchExerciseImageBase()
            val exerciseImages = exerciseService.fetchAllExerciseImages(fetchedExerciseImageBase.count).exerciseImages
            val exercises = exerciseService.fetchAllExercises(fetchedExerciseInfoBase.count).asDatabaseModel()
            exerciseImages.forEach { image ->
                exercises.first { it.exerciseId == image.id }.imageResUrl = image.imageResUrl
            }
            exerciseDao.insertAll(exercises)
            insertNetworkRequestRecordDB(0)
        }
    }



    suspend fun fetchAllNetworkExerciseImages(){
        withContext(Dispatchers.IO){
            val fetchedExerciseImageBase = exerciseService.fetchExerciseImageBase()
            //insertNetworkRequestRecordDB()
        }
    }
    /*suspend fun tryUpdateRecentExerciseCache() {
        if (shouldUpdateExerciseCache())
            fetchExercises()
    }*/


    suspend fun insertExerciseDB(exercise: Exercise) {
        exerciseDao.insert(exercise.asDatabaseModel())
    }

    suspend fun insertNetworkRequestRecordDB(offset: Int) {
        networkRequestRecordsDao.insert(DatabaseNetworkRequestRecords(offset = offset))
    }

    fun getNetworkRequestRecordDB(id: Int) = networkRequestRecordsDao.getNetworkRequestRecord(id)

    suspend fun isNetworkRequestRecordDBEmpty() = networkRequestRecordsDao.isEmpty()

    suspend fun update(exercise: Exercise) {
        exerciseDao.update(exercise.asDatabaseModel())
    }

    suspend fun deleteAllExerciseItems() {
        exerciseDao.deleteAll()
    }

    suspend fun deleteExerciseItem(exercise: Exercise) {
        exerciseDao.delete(exercise.asDatabaseModel())
    }

    suspend fun deleteNetworkRequestRecordDB(requestRecords: DatabaseNetworkRequestRecords) {
        networkRequestRecordsDao.delete(requestRecords)
    }

    suspend fun deleteAllNetworkRequestRecordDB(){
        networkRequestRecordsDao.deleteAll()
    }

    fun retrieveExerciseItem(id: Int) = exerciseDao.getExercise(id).map { it.asDomainModel() }

    companion object {
        @Volatile private var instance: ExerciseRepo? = null

        fun getInstance(exerciseDao: ExerciseDao, networkRequestRecordsDao: DBNetworkRequestRecords, exerciseService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: ExerciseRepo(exerciseDao, networkRequestRecordsDao, exerciseService).also { instance = it }
            }
    }
}

/*
In-memory cache is handled in the repo layer by using either:

LiveData - val exercises: LiveData<List<Exercise>> =  Transformations.map(itemDao.getExercises().asLiveData()) {
        it.asDomainModel()
    }

 */