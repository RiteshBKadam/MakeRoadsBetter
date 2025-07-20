package com.riteshbkadam.makeroadsbetter.screens

import android.app.AlertDialog
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.riteshbkadam.makeroadsbetter.components.RoadConditionDataPopPopUp
import com.riteshbkadam.makeroadsbetter.util.base64ToBitmap
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun UsersContributionMap(navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(18.0)
        }
    }

    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    var showPopup by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("roadReports")
                .get()
                .addOnSuccessListener { result ->
                    result.documents.forEach { doc ->
                        val lat = doc.getDouble("latitude") ?: return@forEach
                        val lon = doc.getDouble("longitude") ?: return@forEach
                        val imageBase64 = doc.getString("image") ?: return@forEach
                        val bitmap = base64ToBitmap(imageBase64)
                        if (bitmap != null) {
                            val point = GeoPoint(lat, lon)
                            val marker = Marker(mapView).apply {
                                position = point
                                title = "My Report"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                                setOnMarkerClickListener { _, _ ->
                                    selectedImage = bitmap
                                    showPopup = true
                                    true
                                }

                                setOnMarkerClickListener { _, _ ->
                                    AlertDialog.Builder(context)
                                        .setTitle("Delete Report")
                                        .setMessage("Do you want to delete this report?")
                                        .setPositiveButton("Yes") { _, _ ->
                                            // Delete from user-specific report collection
                                            firestore.collection("users")
                                                .document(userId)
                                                .collection("roadReports")
                                                .document(doc.id)
                                                .delete()
                                                .addOnSuccessListener {
                                                    mapView.overlays.remove(this)
                                                    mapView.invalidate()
                                                    Toast.makeText(
                                                        context,
                                                        "Report deleted",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to delete report",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                            // Optionally delete from global map
                                            firestore.collection("roadConditionDataMap")
                                                .whereEqualTo("latitude", lat)
                                                .whereEqualTo("longitude", lon)
                                                .get()
                                                .addOnSuccessListener { snapshot ->
                                                    snapshot.documents.forEach {
                                                        it.reference.delete()
                                                    }
                                                }
                                        }
                                        .setNegativeButton("Cancel", null)
                                        .show()
                                    true
                                }
                            }

                            mapView.overlays.add(marker)
                            mapView.controller.animateTo(point)
                            mapView.controller.setZoom(20.0)
                        }
                    }

                    mapView.invalidate()
                }
        }
    }
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column {
                AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back"
                    )
                }
            }

            if (showPopup) {
                RoadConditionDataPopPopUp(
                    onDismiss = { showPopup = false },
                    image = selectedImage,
                    context = context
                )
            }
        }
    }
}
