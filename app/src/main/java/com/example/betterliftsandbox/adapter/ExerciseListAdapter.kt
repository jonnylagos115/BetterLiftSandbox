package com.example.betterliftsandbox.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.betterliftsandbox.databinding.FragmentExercisesListItemBinding
import com.example.betterliftsandbox.viewmodels.ExerciseItemUiState


class ExerciseListAdapter(val clickListener: ExerciseListListener) :
    ListAdapter<ExerciseItemUiState, ExerciseListAdapter.ExerciseViewHolder>(ExerciseListDiffCallback())
{
    private var item_selected_position = -1
    private val TAG = "ExercisesFragAdapter"
    private var selectedListItems = mutableListOf<ExerciseItemUiState>()
    enum class ClickEvent {
        DELETE,
        VIEW
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        Log.d(TAG, "ViewHolder created")
        return ExerciseViewHolder.from(parent)
    }

    class ExerciseViewHolder private constructor(private val binding: FragmentExercisesListItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: ExerciseItemUiState, clickListener: ExerciseListListener) {
            binding.apply {
                exerciseNameTextView.text = item.exerciseName
                muscleGroupNameTextView.text = item.muscleGroupName
                deleteItemButton.setOnClickListener {
                    item.deleteExerciseItem.invoke()
                }
                itemView.setOnClickListener {
                    clickListener.onClick(item)
                }
                /*if (exercise.isSelected == true) {
                    exerciseImageView.setImageResource(R.drawable.icons8_done_50)
                    exercise.isSelected = false
                } else {
                    exerciseImageView.setImageResource(exercise.imageReferenceId)
                }*/
            }
        }

        companion object {
            fun from(parent: ViewGroup): ExerciseViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentExercisesListItemBinding.inflate(layoutInflater, parent, false)
                return ExerciseViewHolder(binding)
            }
        }
    }

    private fun setSelection(adapterPosition: Int) {
        if (adapterPosition == RecyclerView.NO_POSITION) return
        Log.d(TAG, "itemView clicked")
        item_selected_position = adapterPosition
        notifyItemChanged(adapterPosition)
    }

}

class ExerciseListDiffCallback : DiffUtil.ItemCallback<ExerciseItemUiState>() {
    override fun areItemsTheSame(oldItem:ExerciseItemUiState, newItem: ExerciseItemUiState): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: ExerciseItemUiState, newItem: ExerciseItemUiState): Boolean {
        return oldItem.exerciseId == newItem.exerciseId
    }
}

class ExerciseListListener(val clickListener: (exerciseId: Int, exerciseName: String) -> Unit) {
    fun onClick(exercise: ExerciseItemUiState) = clickListener(exercise.exerciseId, exercise.exerciseName)
}