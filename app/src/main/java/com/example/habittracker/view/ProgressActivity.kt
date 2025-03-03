package com.example.habittracker.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.adapter.ProgressAdapter
import com.example.habittracker.databinding.ActivityProgressBinding
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private lateinit var habitViewModel: HabitViewModel
    private lateinit var progressAdapter: ProgressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = HabitRepository()
        val viewModelFactory = HabitViewModelFactory(repository)
        habitViewModel = ViewModelProvider(this, viewModelFactory)[HabitViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        habitViewModel.loadHabits()
    }

    private fun setupRecyclerView() {
        progressAdapter = ProgressAdapter()
        binding.progressList.apply {
            layoutManager = LinearLayoutManager(this@ProgressActivity)
            adapter = progressAdapter
        }
    }

    private fun observeViewModel() {
        habitViewModel.habits.observe(this) { habits ->
            progressAdapter.submitList(habits)
        }
    }
}

