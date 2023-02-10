package com.example.betterliftsandbox.ui.exercise

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.betterliftsandbox.R
import com.example.betterliftsandbox.databinding.FragmentExerciseNewItemBinding
import com.example.betterliftsandbox.utils.exerciseVMInjector
import com.example.betterliftsandbox.viewmodels.ExerciseViewModel

class ExerciseNewItemFragment : Fragment() {

    private var _binding: FragmentExerciseNewItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExerciseViewModel by viewModels {
        exerciseVMInjector.provideExerciseViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseNewItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //bindUi()
    }

    /*private fun bindUi() {
        binding.apply{
            saveAction.setOnClickListener {
                addNewItem()
            }
            val muscleTypes = resources.getStringArray(R.array.muscle_groups)
            val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_item_muscle_group_type, muscleTypes)
            selectedItemTextView.setAdapter(arrayAdapter)
        }

    }
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemExerciseName.text.toString(),
            binding.selectedItemTextView.text.toString())
    }

    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.itemExerciseName.text.toString(),
                binding.selectedItemTextView.text.toString()
            )
        }
        val action = ExerciseNewItemFragmentDirections.actionExerciseNewItemFragmentToNavExercise()
        findNavController().navigate(action)
    }*/

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}