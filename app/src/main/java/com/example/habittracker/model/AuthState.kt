package com.example.habittracker.model

sealed class AuthState {
    object AUTHENTICATED : AuthState()
    object UNAUTHENTICATED : AuthState()
    object PASSWORD_RESET_SENT : AuthState()
    object LOADING : AuthState()
    data class ERROR(val message: String) : AuthState()
}
