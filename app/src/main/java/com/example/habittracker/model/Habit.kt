package com.example.habittracker.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Habit(
    val id: String = "",
    val name: String = "",
    val userId: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    var streak: Int = 0,
    var isCompleted: Boolean = false,
    var lastCompletedDate: Long = 0,
    var progress: Int = 0
)

enum class HabitType {
    WATER, SLEEP, MEDITATE, READ_BOOK, EXERCISE
}