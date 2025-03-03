package com.example.habittracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.databinding.ItemHabitBinding
import com.example.habittracker.model.Habit

class HabitAdapter(private val onHabitCompleted: (Habit) -> Unit) :
    ListAdapter<Habit, HabitAdapter.HabitViewHolder>(HabitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding, onHabitCompleted)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class HabitViewHolder(
        private val binding: ItemHabitBinding,
        private val onHabitCompleted: (Habit) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(habit: Habit) {
            binding.apply {
                habitName.text = habit.name
                habitDescription.text = habit.description
                habitDate.text = habit.date
                habitTime.text = habit.time

                // Remove the previous listener to avoid duplicates
                habitCheckBox.setOnCheckedChangeListener(null)

                // Set the current state
                habitCheckBox.isChecked = habit.isCompleted

                // Set new listener
                habitCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked && !habit.isCompleted) {  // Only trigger if changing from uncompleted to completed
                        onHabitCompleted(habit)
                    }
                }

                executePendingBindings()
            }
        }
    }


    class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Habit, newItem: Habit) = oldItem == newItem
    }


}