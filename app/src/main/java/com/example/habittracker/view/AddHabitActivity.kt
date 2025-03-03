package com.example.habittracker.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.R
import com.example.habittracker.databinding.ActivityAddHabitBinding
import com.example.habittracker.model.Habit
import com.example.habittracker.model.HabitType
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import java.util.*

class AddHabitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHabitBinding
    private lateinit var habitViewModel: HabitViewModel
    private var selectedDate: String = ""
    private var selectedTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize HabitRepository and ViewModel
        val habitRepository = HabitRepository()  // Replace with actual repository initialization
        val viewModelFactory = HabitViewModelFactory(habitRepository)
        habitViewModel = ViewModelProvider(this, viewModelFactory).get(HabitViewModel::class.java)

        // Select Date
        binding.selectDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    binding.selectedDateTextView.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Select Time
        binding.selectTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    binding.selectedTimeTextView.text = selectedTime
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Add Habit
        binding.addHabitButton.setOnClickListener {

            val habitName = binding.habitNametxt.text.toString()
            val habitDescription = binding.habitDescription.text.toString()

            if (habitName.isNotBlank() && selectedDate.isNotBlank() && selectedTime.isNotBlank()) {
                val selectedHabits = mutableListOf<HabitType>()
                if (binding.waterCheckBox.isChecked) selectedHabits.add(HabitType.WATER)
                if (binding.sleepCheckBox.isChecked) selectedHabits.add(HabitType.SLEEP)
                if (binding.meditateCheckBox.isChecked) selectedHabits.add(HabitType.MEDITATE)
                if (binding.readCheckBox.isChecked) selectedHabits.add(HabitType.READ_BOOK)
                if (binding.exerciseCheckBox.isChecked) selectedHabits.add(HabitType.EXERCISE)

                selectedHabits.forEach { habitType ->
                    val habit = Habit(
                        id = UUID.randomUUID().toString(),  // Generate or assign unique ID
                        name = habitType.name.toLowerCase().capitalize(),
                        userId = "",  // Replace with actual user ID
                        description = habitDescription,  // Use the user-provided description
                        date = selectedDate,  // Pass selected date
                        time = selectedTime   // Pass selected time
                    )

                    habitViewModel.addHabit(habit)  // Add habit to ViewModel or repository
                }

                Toast.makeText(this, "Habit(s) added successfully", Toast.LENGTH_SHORT).show()
                finish()  // Close the activity after adding the habit
            } else {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
