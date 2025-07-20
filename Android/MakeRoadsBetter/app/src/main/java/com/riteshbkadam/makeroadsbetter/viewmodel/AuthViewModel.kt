package com.riteshbkadam.makeroadsbetter.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var currentUser = auth.currentUser

    fun signUpWithEmailPassword(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                        onSuccess()
                    } else {
                        onFailure(task.exception?.message ?: "Sign Up failed")
                    }
                }
        }
    }

    fun loginWithEmailPassword(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (auth.currentUser?.isEmailVerified == true) {
                            onSuccess()
                        } else {
                            onFailure("Email not verified. Please verify your email.")
                        }
                    } else {
                        onFailure(task.exception?.message ?: "Login failed")
                    }
                }
        }
    }

    fun logout(navController: NavController) {
        auth.signOut()
        navController.navigate("auth") {
            popUpTo(0) { inclusive = true } // clears back stack
            launchSingleTop = true
        }
    }
}
