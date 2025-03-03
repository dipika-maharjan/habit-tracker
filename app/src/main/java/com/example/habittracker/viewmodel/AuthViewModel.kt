package com.example.habittracker .viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        _authState.value = if (auth.currentUser != null) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.AUTHENTICATED
            } catch (e: Exception) {
                _authState.value = AuthState.ERROR(e.message ?: "Sign up failed")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.AUTHENTICATED
            } catch (e: Exception) {
                _authState.value = AuthState.ERROR(e.message ?: "Sign in failed")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.UNAUTHENTICATED
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PASSWORD_RESET_SENT
            } catch (e: Exception) {
                _authState.value = AuthState.ERROR(e.message ?: "Password reset failed")
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}

sealed class AuthState {
    object AUTHENTICATED : AuthState()
    object UNAUTHENTICATED : AuthState()
    object PASSWORD_RESET_SENT : AuthState()
    data class ERROR(val message: String) : AuthState()
}

