package com.riteshbkadam.makeroadsbetter.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.riteshbkadam.makeroadsbetter.screens.AuthScreen
import com.riteshbkadam.makeroadsbetter.screens.HomeScreen
import com.riteshbkadam.makeroadsbetter.screens.OsmMapView
import com.riteshbkadam.makeroadsbetter.screens.SplashScreen
import com.riteshbkadam.makeroadsbetter.screens.UsersContributionMap
import com.riteshbkadam.makeroadsbetter.viewmodel.AuthViewModel
import com.riteshbkadam.makeroadsbetter.viewmodel.LocationViewModel

@Composable
fun NavigationGraph(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController, auth)
        }
        composable("auth") {
            AuthScreen(viewModel = viewModel(), onAuthSuccess = {
                navController.navigate("home") {
                    popUpTo("auth") { inclusive = true }
                }
            })
        }
        composable("home") {
            val viewModel: AuthViewModel = viewModel()
            HomeScreen(navController, viewModel )
        }
        composable("map") {
            val viewModel: LocationViewModel = viewModel()
            OsmMapView(viewModel=viewModel,navController)
        }
        composable("userReports") {
            UsersContributionMap(navController = navController)
        }
    }
}
