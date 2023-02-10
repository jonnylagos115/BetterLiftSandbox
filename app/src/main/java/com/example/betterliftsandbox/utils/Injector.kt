package com.example.betterliftsandbox.utils

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.example.betterliftsandbox.database.AppDatabase
import com.example.betterliftsandbox.network.NetworkService
import com.example.betterliftsandbox.repository.ExerciseRepo
import com.example.betterliftsandbox.viewmodels.ExerciseViewModel


interface ExerciseViewModelFactoryProvider {
    fun provideExerciseViewModelFactory(context: Context): ExerciseViewModel.ExerciseViewModelFactory
}

val exerciseVMInjector: ExerciseViewModelFactoryProvider
    get() = currentExerciseVMInjector

private object ExerciseViewModelProvider: ExerciseViewModelFactoryProvider {
    private fun getExerciseRepo(context: Context): ExerciseRepo {
        return ExerciseRepo.getInstance(
            exerciseDao(context),
            networkRequestRecordsDao(context),
            exerciseService()
        )
    }

    private  fun exerciseService() = NetworkService()

    private fun exerciseDao(context: Context) =
        AppDatabase.getInstance(context.applicationContext).exerciseDao()

    private fun networkRequestRecordsDao(context: Context) =
        AppDatabase.getInstance(context.applicationContext).networkRequestRecordsDao()

    override fun provideExerciseViewModelFactory(context: Context): ExerciseViewModel.ExerciseViewModelFactory {
        val repository = getExerciseRepo(context)
        return ExerciseViewModel.ExerciseViewModelFactory(repository)
    }
}

private object Lock

@Volatile private var currentExerciseVMInjector: ExerciseViewModelFactoryProvider =
    ExerciseViewModelProvider

@VisibleForTesting
private fun setInjectorForTesting(injector: ExerciseViewModelFactoryProvider?) {
    synchronized(Lock) {
        currentExerciseVMInjector = injector ?: ExerciseViewModelProvider
    }
}