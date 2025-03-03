package com.example.habittracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.databinding.ItemProgressBinding
import com.example.habittracker.model.Habit

class ProgressAdapter : ListAdapter<Habit, ProgressAdapter.ProgressViewHolder>(ProgressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val binding = ItemProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProgressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProgressViewHolder(private val binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(habit: Habit) {
            binding.apply {
                // Make sure the habit name is visible and properly set
                habitName.text = habit.name.capitalize()  // Capitalize the first letter

                // Update progress bar and text
                progressBar.max = 21  // Set maximum to 21 days
                progressBar.progress = habit.progress
                progressText.text = "${habit.progress}/21 days"

                // Add description if needed
                habitDescription.text = habit.description

                executePendingBindings()
            }
        }
    }

    class ProgressDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Habit, newItem: Habit) = oldItem == newItem
    }
}