package com.example.habittracker.repository

import com.example.habittracker.model.Habit
import com.example.habittracker.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.Query

class HabitRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val habitsCollection: CollectionReference = firestore.collection("habits")
    private val usersCollection: CollectionReference = firestore.collection("users")

    suspend fun getHabits(): List<Habit> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = habitsCollection.get().await()
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(Habit::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addHabit(habit: Habit) = withContext(Dispatchers.IO) {
        try {
            habitsCollection.add(habit).await()
        } catch (e: Exception) {
            throw e
        }
    }



    suspend fun updateHabit(habit: Habit) = withContext(Dispatchers.IO) {
        try {
            habitsCollection.document(habit.id).set(habit).await()
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun updateUserPoints(userId: String, pointsToAdd: Int) = withContext(Dispatchers.IO) {
        try {
            // Use Firebase transaction to ensure atomic update of points
            firestore.runTransaction { transaction ->
                val userDoc = transaction.get(usersCollection.document(userId))
                val currentPoints = userDoc.getLong("points") ?: 0
                transaction.update(
                    usersCollection.document(userId),
                    "points",
                    currentPoints + pointsToAdd
                )
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun markHabitCompleted(habit: Habit, userId: String) = withContext(Dispatchers.IO) {
        try {
            // Update habit completion status
            habitsCollection.document(habit.id).update("completed", true).await()

            // Add points to the user
            firestore.runTransaction { transaction ->
                val userDoc = transaction.get(usersCollection.document(userId))
                val currentPoints = userDoc.getLong("points") ?: 0
                transaction.update(usersCollection.document(userId), "points", currentPoints + 10)
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getLeaderboard(): List<User> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = usersCollection
                .orderBy("points", Query.Direction.DESCENDING)
                .get()
                .await()
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(User::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}


