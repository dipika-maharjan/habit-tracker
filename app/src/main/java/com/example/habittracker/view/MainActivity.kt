package com.example.habittracker.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.R
import com.example.habittracker.databinding.ActivityMainBinding
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.viewmodel.AuthState
import com.example.habittracker.viewmodel.AuthViewModel
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import com.example.habittracker.adapter.HabitAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var habitViewModel: HabitViewModel
    private lateinit var habitAdapter: HabitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        habitViewModel = ViewModelProvider(
            this,
            HabitViewModelFactory(HabitRepository())
        ).get(HabitViewModel::class.java)

        binding.authViewModel = authViewModel
        binding.habitViewModel = habitViewModel
        binding.lifecycleOwner = this

        setupRecyclerView()
        setupBottomNavigation()
        setupObservers()
        setupAddHabitButton()
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter { habit ->
            habitViewModel.updateHabitProgressAndPoints(habit)
        }
        binding.habitList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = habitAdapter
        }

        habitViewModel.habits.observe(this) { habits ->
            habitAdapter.submitList(habits)
            if (habits.isNotEmpty()) {
                binding.habitList.visibility = View.VISIBLE

            } else {
                binding.habitList.visibility = View.GONE

            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> true
                R.id.nav_progress -> {
                    startActivity(Intent(this, ProgressActivity::class.java))
                    true
                }
                R.id.nav_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    true
                }
                R.id.nav_logout -> {
                    authViewModel.signOut()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupObservers() {
        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.AUTHENTICATED -> {
                    showMainContent()
                    habitViewModel.loadHabits() // Ensure we load habits once authenticated
                }
                is AuthState.UNAUTHENTICATED -> showLoginForm()
                is AuthState.PASSWORD_RESET_SENT -> showPasswordResetSentMessage()
                is AuthState.ERROR -> showError(state.message)
            }
        }

        habitViewModel.error.observe(this) { error ->
            error?.let { showError(it) }
        }
    }

    private fun setupAddHabitButton() {
        binding.addHabitButton.setOnClickListener {
            startActivity(Intent(this, AddHabitActivity::class.java))
        }
    }

    private fun showMainContent() {
        binding.authForm.visibility = View.GONE
        binding.habitList.visibility = View.VISIBLE
        binding.addHabitButton.visibility = View.VISIBLE
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    private fun showLoginForm() {
        binding.authForm.visibility = View.VISIBLE
        binding.habitList.visibility = View.GONE
        binding.addHabitButton.visibility = View.GONE
        binding.bottomNavigation.visibility = View.GONE
    }

    private fun showPasswordResetSentMessage() {
        Toast.makeText(this, getString(R.string.password_reset_sent), Toast.LENGTH_LONG).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

