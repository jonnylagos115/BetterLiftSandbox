package com.example.betterliftsandbox.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.betterliftsandbox.R
import com.example.betterliftsandbox.databinding.FragmentExercisesListItemBinding
import com.example.betterliftsandbox.domain.Exercise


class ExerciseListAdapter(private val clickEvent: (ClickEvent, Exercise) -> Unit) :
    ListAdapter<Exercise, ExerciseListAdapter.ExerciseViewHolder>(DiffCallback)
{
    private var item_selected_position = -1
    private val TAG = "ExercisesFragAdapter"
    private var selectedListItems = mutableListOf<Exercise>()
    enum class ClickEvent {
        DELETE,
        VIEW
    }

    inner class ExerciseViewHolder(private val binding: FragmentExercisesListItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        init {
            binding.deleteItemButton.setOnClickListener {
                clickEvent(ClickEvent.DELETE, getItem(bindingAdapterPosition))
            }
        }
        fun bind(exercise: Exercise) {
            binding.apply {
                exerciseNameTextView.text = exercise.exerciseName
                muscleGroupNameTextView.text = exercise.muscleGroupName
                /*if (exercise.isSelected == true) {
                    exerciseImageView.setImageResource(R.drawable.icons8_done_50)
                    exercise.isSelected = false
                } else {
                    exerciseImageView.setImageResource(exercise.imageReferenceId)
                }*/
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentExercisesListItemBinding.inflate(layoutInflater, parent, false)
        Log.d(TAG, "New viewholder created")
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val current = getItem(position)

        holder.itemView.setOnClickListener {
            clickEvent(ClickEvent.VIEW, current)
        }
        holder.bind(current)
    }

    private fun setSelection(adapterPosition: Int) {
        if (adapterPosition == RecyclerView.NO_POSITION) return
        Log.d(TAG, "itemView clicked")
        item_selected_position = adapterPosition
        notifyItemChanged(adapterPosition)
    }

    fun getSelectedItems(): MutableList<Exercise> {
        for (exercise in selectedListItems){
            exercise.isSelected = false
        }
        return selectedListItems
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(oldItem:Exercise, newItem: Exercise): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
                return oldItem.exerciseId == newItem.exerciseId
            }
        }
    }
}