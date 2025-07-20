package com.riteshbkadam.makeroadsbetter.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController, auth: FirebaseAuth) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(2000) // Optional splash delay

        val user = auth.currentUser
        if (user != null && user.isEmailVerified) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("auth") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Make Roads Better",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )
    }
}
