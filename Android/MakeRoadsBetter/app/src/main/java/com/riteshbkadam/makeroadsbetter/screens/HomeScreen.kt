package com.riteshbkadam.makeroadsbetter.screens


import UserGuidePopup
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.riteshbkadam.makeroadsbetter.viewmodel.AuthViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: AuthViewModel) {
    RequestPermissions {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Logout Button (Top-Left)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        IconButton(
                            onClick = { viewModel.logout(navController) },
                            modifier = Modifier.width(88.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.wrapContentSize(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(35.dp)
                                )
                                Text("Log out", color = Color.Red, fontSize = 15.sp)
                            }
                        }
                    }
                }

                // Main Action Buttons (Center)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(25.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Make Roads Better", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { navController.navigate("map") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contribute Report")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("userReports") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Show Contributions")
                    }
                }

                // Info Button (Bottom-Right)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 45.dp, end = 25.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    var showGuide by remember { mutableStateOf(false) }
                    Button(
                        onClick = { showGuide = true },
                        shape = CircleShape,
                        modifier = Modifier.size(55.dp)
                    ) {
                        Text("i", fontSize = 17.sp)
                    }
                    if (showGuide) {
                        UserGuidePopup(onDismiss = { showGuide = false })
                    }
                }
            }
        }
    }
}

@Composable
fun RequestPermissions(content: @Composable () -> Unit) {
    val context = LocalContext.current

    val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    ).filter {
        if (it == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
        } else if (it == Manifest.permission.READ_EXTERNAL_STORAGE) {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q
        } else {
            true
        }
    }.toTypedArray()

    var allPermissionsGranted by remember {
        mutableStateOf(
            requiredPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }


    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        allPermissionsGranted = permissions.all { it.value }
    }

    LaunchedEffect(Unit) {
        if (!allPermissionsGranted) {
            launcher.launch(requiredPermissions)
        }
    }

    if (allPermissionsGranted) {
        content()
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Please grant required permissions to continue")
        }
    }
}