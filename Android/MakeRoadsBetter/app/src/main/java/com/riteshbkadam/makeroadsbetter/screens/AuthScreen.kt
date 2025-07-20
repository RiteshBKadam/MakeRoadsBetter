package com.riteshbkadam.makeroadsbetter.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.riteshbkadam.makeroadsbetter.viewmodel.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel, onAuthSuccess: () -> Unit) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (isLogin) "Login" else "Sign Up",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                if (isLogin) {
                    viewModel.loginWithEmailPassword(email, password,
                        onSuccess = {
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            onAuthSuccess()
                        },
                        onFailure = {
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    viewModel.signUpWithEmailPassword(email, password,
                        onSuccess = {
                            Toast.makeText(context, "Verification email sent", Toast.LENGTH_LONG).show()
                            isLogin = true
                        },
                        onFailure = {
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }) {
                Text(if (isLogin) "Login" else "Sign Up")
            }
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = { isLogin = !isLogin }) {
                Text(if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Login")
            }
        }
    }
}
