package com.example.betterliftsandbox.ui.exercise

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betterliftsandbox.R
import com.example.betterliftsandbox.adapter.ExerciseListAdapter
import com.example.betterliftsandbox.adapter.ExerciseListListener
import com.example.betterliftsandbox.databinding.FragmentExerciseBinding
import com.example.betterliftsandbox.viewmodels.ExerciseViewModel
import com.example.betterliftsandbox.utils.exerciseVMInjector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExerciseFragment : Fragment() {

    private val TAG = "ExerciseFragment"
    private var _binding: FragmentExerciseBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: ExerciseViewModel by viewModels {
        exerciseVMInjector.provideExerciseViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Retrieve and inflate the layout for this fragment
        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        val adapter = ExerciseListAdapter(ExerciseListListener { exerciseItemId, exerciseName ->
            viewModel.onExerciseItemClicked(exerciseItemId, exerciseName)
        })
        binding.exercisesList.adapter = adapter
        subscribeUi(adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: ExerciseListAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { currentState ->
                    adapter.submitList(currentState.exercisesItems)
                    if (currentState.navigateToExerciseDetail != null){
                        val action = ExerciseFragmentDirections.actionNavExerciseToNavExerciseDetail(
                            currentState.navigateToExerciseDetail,
                            currentState.exerciseDetailLabel
                        )
                        findNavController().navigate(action)
                        viewModel.onExerciseDetailNavigated()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.

        binding.exercisesList.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)

        // Add menu items without using the FragmentType Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.exercise_detail_action_create_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.action_create -> {
                        val action = ExerciseFragmentDirections.actionNavExerciseToExerciseNewItemFragment()
                        findNavController().navigate(action)
                    }
                    R.id.action_delete_all -> {
                        viewModel.deleteExerciseItems()
                        viewModel.deleteAllNetworkRequestRecordDB()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * Frees the binding object when the FragmentType is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}