package com.example.betterliftsandbox.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.betterliftsandbox.R
import com.example.betterliftsandbox.databinding.FragmentExerciseDetailBinding
import com.example.betterliftsandbox.domain.Exercise
import com.example.betterliftsandbox.viewmodels.ExerciseViewModel
import com.example.betterliftsandbox.utils.exerciseVMInjector
import kotlinx.coroutines.launch

class ExerciseDetailFragment : Fragment() {

    private var _binding: FragmentExerciseDetailBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val navArgs: ExerciseDetailFragmentArgs by navArgs()
    private val viewModel: ExerciseViewModel by viewModels {
        exerciseVMInjector.provideExerciseViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val exerciseId = navArgs.exerciseId
        collectUiState(exerciseId)
    }

    private fun collectUiState(exerciseId: Int){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.retrieveExerciseItem(exerciseId).collect { selectedExerciseItem ->
                    if (selectedExerciseItem != null) {
                        bind(selectedExerciseItem)
                    }
                }
            }
        }
    }

    private fun bind(exercise: Exercise) {
        binding.apply {
            bindImage(exerciseImage, exercise.imageResUrl)
            var incre = 0
            exerciseInstructionsText.text = exercise.description.split(". ").joinToString(".\n") {
                incre++
                "$incre. $it"
            }
        }
    }

    private fun bindImage(imgView: ImageView, imgUrl: String?){
        if (imgUrl != null){
            imgUrl.let {
                val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
                imgView.load(imgUri) {
                    placeholder(R.drawable.loading_animation)
                }
            }
        } else{
            imgView.setImageResource(R.drawable.chestpressing)
        }
    }

    private fun parseInstructionList() {

    }
    /**
     * Frees the binding object when the FragmentType is destroyed.
     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}