package com.example.betterliftsandbox.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ExerciseDao {

    // Exercise Schema methods
    @Query("select * from databaseexercise")
    fun getLiveExercises(): Flow<List<DatabaseExercise>>

    @Query("SELECT * from databaseexercise WHERE exerciseId = :id")
    fun getExercise(id: Int): Flow<DatabaseExercise>

    @Query("SELECT * FROM databaseexercise ORDER BY exerciseId LIMIT 1")
    fun loadLastTask(): Flow<DatabaseExercise?>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exercise: DatabaseExercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercise: List<DatabaseExercise>)

    @Update
    suspend fun update(exercise: DatabaseExercise)

    @Delete
    suspend fun delete(exercise: DatabaseExercise)

    @Query("DELETE FROM databaseexercise")
    suspend fun deleteAll()

}

@Dao
interface DBNetworkRequestRecords {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(requestRecords: DatabaseNetworkRequestRecords)

    @Delete
    suspend fun delete(requestRecords: DatabaseNetworkRequestRecords)

    @Query("DELETE FROM db_network_request_records")
    suspend fun deleteAll()

    @Query("SELECT (SELECT COUNT(*) FROM db_network_request_records) == 0")
    suspend fun isEmpty(): Boolean

    @Query("SELECT * FROM db_network_request_records WHERE id = :id")
    fun getNetworkRequestRecord(id: Int): Flow<DatabaseNetworkRequestRecords>



}

/*
  Whenever you change the schema of the database table, you'll have to increase the version number.
*/
@Database(entities = [DatabaseExercise::class, DatabaseNetworkRequestRecords::class], version = 2, exportSchema = false)
@TypeConverters(ExerciseListTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun networkRequestRecordsDao(): DBNetworkRequestRecords

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).fallbackToDestructiveMigration().build()
        }
    }
}

private const val DATABASE_NAME = "betterlift-db"

