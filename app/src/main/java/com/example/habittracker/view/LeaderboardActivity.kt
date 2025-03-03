package com.example.habittracker.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.adapter.LeaderboardAdapter
import com.example.habittracker.databinding.ActivityLeaderboardBinding
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var habitViewModel: HabitViewModel
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = HabitRepository()
        val viewModelFactory = HabitViewModelFactory(repository)
        habitViewModel = ViewModelProvider(this, viewModelFactory)[HabitViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        habitViewModel.loadLeaderboard()
    }

    private fun setupRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter()
        binding.leaderboardList.apply {
            layoutManager = LinearLayoutManager(this@LeaderboardActivity)
            adapter = leaderboardAdapter
        }
    }

    private fun observeViewModel() {
        habitViewModel.leaderboard.observe(this) { users ->
            leaderboardAdapter.submitList(users)
        }

        habitViewModel.error.observe(this) { error ->
            // Handle error (e.g., show a toast or snackbar)
        }
    }
}

