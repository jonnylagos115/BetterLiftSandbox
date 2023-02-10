package com.example.betterliftsandbox.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.betterliftsandbox.domain.Exercise
import com.example.betterliftsandbox.repository.ExerciseRepo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * View Model to keep a reference to the Exercise repository and an up-to-date list of all items.
 *
 */
private const val ITEMS_PER_PAGE = 50
private const val TAG = "ExerciseVM"
class ExerciseViewModel internal constructor(
    private val exerciseRepo: ExerciseRepo
) : ViewModel() {

   /* private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()*/

    val exerciseItems: StateFlow<List<Exercise>> = exerciseRepo.exerciseStream.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf()
        )

    //(TODO: Figure out how to set up the Exercise here for the recyclerview adapter to use
    // The fields of ExercisesUiState contains a list of Exercise, the idea is that when UI collects
    // from uiState, it should then pass the list of Exercise to 'adapter.submitList(currentState.exercisesItems)'
    // What I'm confused about is how I create the instances of a list of Exercise from 'this' stateholder
    // Should it be a stream then converted to a list? Or a stream, to a stateflow, then to a list? I need to research how uiState
    // can obtain a guarantee of atomic list of Exercise)

   /* val exerciseItems: Flow<PagingData<Exercise>> = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = {exerciseRepo.exercisePagingSource() }
    )
        .flow
        .cachedIn(viewModelScope)*/

    init {
        refreshDataFromRepository()
    }



    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            if (exerciseRepo.isNetworkRequestRecordDBEmpty()){
                try {
                    exerciseRepo.fetchAllNetworkExercises()
                    Log.d(TAG, "Network get request made")
                } catch (networkError: IOException) {
                    Log.d(TAG, "Network get request failed")
                }
            }
        }
    }


    /*fun fetchAllExercisesFromDatabase() {
        viewModelScope.launch {
            val exerciseList = exerciseRepo.exerciseStream.first().asExerciseItemUiState()
            _uiState.update { currentState ->
                currentState.copy(exercisesItems = exerciseList)
            }
        }
    }

    fun List<Exercise>.asExerciseItemUiState(): List<ExerciseItemUiState> {
        return map {
            ExerciseItemUiState(
                name = it.exerciseName,
                muscleGroupName = it.muscleGroupName,
                imageResId = it.imageReferenceId,
                onClickDetailedExerciseAction =
            )
        }
    }*/

    fun retrieveExerciseItem(id: Int): StateFlow<Exercise?> {
        return exerciseRepo.retrieveExerciseItem(id).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )
    }

    fun deleteExerciseItem(exerciseItem: Exercise) {
        viewModelScope.launch {
            exerciseRepo.deleteExerciseItem(exerciseItem)
        }
    }

    fun isEntryValid(exerciseName: String, muscleGroupName: String): Boolean {
        if (exerciseName.isBlank() || muscleGroupName.isBlank()) {
            return false
        }
        return true
    }

    private fun insertExerciseItem(exercise: Exercise) {
        viewModelScope.launch {
            exerciseRepo.insertExerciseDB(exercise)
        }
    }

   /* private fun getNewItemEntry(
        exerciseName: String,
        muscleGroupName: String): Exercise {
        return DatabaseExercise(
            exerciseName = exerciseName,
            muscleGroupName = muscleGroupName,
            description =
        ).asDomainModel()
    }

    fun addNewItem(
        exerciseName: String,
        muscleGroupName: String) {
        val newItem = getNewItemEntry(exerciseName, muscleGroupName)
        insertExerciseItem(newItem)
    }*/

    private fun deleteAllExerciseItems() {
        viewModelScope.launch {
            exerciseRepo.deleteAllExerciseItems()
        }
    }

    fun deleteExerciseItems() {
        deleteAllExerciseItems()
    }

    fun deleteAllNetworkRequestRecordDB() {
        viewModelScope.launch {
            exerciseRepo.deleteAllNetworkRequestRecordDB()
        }
    }

    private fun updateItem(exercise: Exercise) {
        viewModelScope.launch {
            exerciseRepo.update(exercise)
        }
    }

    class ExerciseViewModelFactory(
        private val repository: ExerciseRepo
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = ExerciseViewModel(repository) as T
    }
}

/*data class ExercisesUiState(
    //val exercisesItems: List<ExerciseItemUiState> = listOf()
)

data class ExerciseItemUiState(
    val id: Long,
    val name: String,
    val muscleGroupName: String,
    @DrawableRes val imageResId: Int,
    val onClickDetailedExerciseAction: () -> Unit
)*/

