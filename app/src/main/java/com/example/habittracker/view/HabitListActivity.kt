package com.example.habittracker.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.R
import com.example.habittracker.adapter.HabitAdapter
import com.example.habittracker.databinding.ActivityHabitListBinding
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory

class HabitListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHabitListBinding
    private lateinit var habitViewModel: HabitViewModel
    private lateinit var habitAdapter: HabitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_habit_list)

        // Initialize the ViewModel with the HabitRepository
        val repository = HabitRepository()
        habitViewModel = ViewModelProvider(
            this,
            HabitViewModelFactory(repository)
        ).get(HabitViewModel::class.java)

        binding.habitViewModel = habitViewModel
        binding.lifecycleOwner = this

        setupRecyclerView()
        setupObservers()
        setupAddHabitButton()

        // Load habits from Firestore
        habitViewModel.loadHabits()
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter { habit ->
            habitViewModel.updateHabitProgressAndPoints(habit)
        }
        binding.habitList.apply {
            layoutManager = LinearLayoutManager(this@HabitListActivity)
            adapter = habitAdapter
        }
    }

    private fun setupObservers() {
        // Observe habits LiveData
        habitViewModel.habits.observe(this) { habits ->
            // Submit the updated list to the adapter
            habitAdapter.submitList(habits)
        }

        // Observe any errors (optional)
        habitViewModel.error.observe(this) { error ->
            error?.let {
                // Handle error if any
            }
        }
    }

    private fun setupAddHabitButton() {
        binding.addHabitButton.setOnClickListener {
            // Navigate to AddHabitActivity
            startActivity(Intent(this, AddHabitActivity::class.java))
        }
    }
}

