package com.example.habittracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.model.Habit
import com.example.habittracker.model.User
import com.example.habittracker.repository.HabitRepository
import kotlinx.coroutines.launch
import java.util.*

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> = _habits

    private val _leaderboard = MutableLiveData<List<User>>()
    val leaderboard: LiveData<List<User>> = _leaderboard

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadHabits()
        loadLeaderboard()
    }

    fun loadHabits() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val habitsList = repository.getHabits()
                _habits.value = habitsList
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            try {
                val users = repository.getLeaderboard()
                _leaderboard.value = users
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            try {
                repository.addHabit(habit)
                loadHabits()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }



    fun updateHabitProgressAndPoints(habit: Habit) {
        viewModelScope.launch {
            try {
                val currentDate = Calendar.getInstance().timeInMillis
                val daysSinceLastCompleted = if (habit.lastCompletedDate == 0L) {
                    1L // First time completion
                } else {
                    (currentDate - habit.lastCompletedDate) / (24 * 60 * 60 * 1000)
                }

                if (!habit.isCompleted || daysSinceLastCompleted >= 1) {
                    val updatedHabit = habit.copy(
                        isCompleted = true,
                        lastCompletedDate = currentDate,
                        progress = if (habit.progress < 21) habit.progress + 1 else 21,
                        streak = habit.streak + 1
                    )

                    // Update the habit first
                    repository.updateHabit(updatedHabit)

                    // Update user points only if progress increased
                    if (updatedHabit.progress > habit.progress) {
                        repository.updateUserPoints(habit.userId, 10)
                    }

                    // Reload data to update UI
                    loadHabits()
                    loadLeaderboard()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}