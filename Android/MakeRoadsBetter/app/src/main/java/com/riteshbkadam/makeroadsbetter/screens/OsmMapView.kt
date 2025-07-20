package com.riteshbkadam.makeroadsbetter.screens

import android.graphics.Bitmap
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.riteshbkadam.makeroadsbetter.components.RoadConditionDataPopPopUp
import com.riteshbkadam.makeroadsbetter.util.LocationUtils
import com.riteshbkadam.makeroadsbetter.util.base64ToBitmap
import com.riteshbkadam.makeroadsbetter.util.bitmapToBase64
import com.riteshbkadam.makeroadsbetter.util.resizeBitmap
import com.riteshbkadam.makeroadsbetter.viewmodel.LocationViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.IOException
import java.util.Locale

@Composable
fun OsmMapView(viewModel: LocationViewModel, navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val geoPoints = remember { mutableStateListOf<GeoPoint>() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var showPopup by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    val routePoints = remember { mutableStateListOf<GeoPoint>() }
    var hasCenteredToUser by remember { mutableStateOf(false) }
    var userLocationMarker by remember { mutableStateOf<Marker?>(null) }
    var showTooltips by remember { mutableStateOf(false) }
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid
    val userName = user?.displayName
    val userEmail = user?.email
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }

    // Start location updates
    LaunchedEffect(Unit) {
        LocationUtils(context).requestLocationUpdates(viewModel)
    }

    val location = viewModel.location.value

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(18.0)
        }
    }

    LaunchedEffect(location) {
        val originalDrawable =
            ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.person)

        val width = 160
        val height = 160

        val bitmap = createBitmap(width, height)
        val canvas = android.graphics.Canvas(bitmap)
        originalDrawable?.setBounds(0, 0, width, height)
        originalDrawable?.draw(canvas)

        val scaledDrawable = bitmap.toDrawable(context.resources)
        if (location != null) {
            val userGeoPoint = GeoPoint(location.latitude, location.longitude)

            if (!hasCenteredToUser) {
                mapView.controller.animateTo(userGeoPoint)
                hasCenteredToUser = true
            }

            if (userLocationMarker == null) {
                userLocationMarker = Marker(mapView).apply {
                    position = userGeoPoint
                    title = "You are here"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = scaledDrawable
                }
                mapView.overlays.add(userLocationMarker)
            } else {

                userLocationMarker?.position = userGeoPoint
                mapView.overlays.remove(userLocationMarker)
                mapView.overlays.add(userLocationMarker)
                mapView.controller.animateTo(userGeoPoint)
                mapView.controller.zoomTo(30.0)
                userLocationMarker?.setOnMarkerClickListener { marker, _ ->
                    Toast.makeText(context, "You are here!", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            mapView.invalidate()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            selectedPoint?.let { point ->
                val resizedBitmap = resizeBitmap(it, 300, 300)
                val base64 = bitmapToBase64(resizedBitmap)

                val timestamp = System.currentTimeMillis()

                if (userId != null) {
                    val userRef = firestore.collection("users").document(userId)

                    userRef.set(
                        mapOf(
                            "name" to (userName ?: "Unknown"),
                            "email" to (userEmail ?: "Unknown")
                        ),
                        SetOptions.merge()
                    )


                    val report = mapOf(
                        "latitude" to point.latitude,
                        "longitude" to point.longitude,
                        "image" to base64,
                        "timestamp" to timestamp
                    )
                    firestore.collection("roadConditionDataMap").add(report)

                    userRef.collection("roadReports")
                        .add(report)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Report saved", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to save report", Toast.LENGTH_SHORT)
                                .show()
                        }

                    // Optional: add marker
                    val marker = Marker(mapView).apply {
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "New Report"
                    }
                    mapView.overlays.add(marker)
                    mapView.invalidate()
                } else {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                }


            }
        }
    }

    LaunchedEffect(Unit) {
        firestore.collection("roadConditionDataMap")
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
                            title = "Road Issue"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setOnMarkerClickListener { _, _ ->
                                selectedImage = bitmap
                                showPopup = true
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }
                }
                mapView.invalidate()
            }
    }

    LaunchedEffect(Unit) {
        firestore.collection("roadPolylines")
            .get()
            .addOnSuccessListener { result ->
                result.documents.forEach { doc ->
                    val routeArray =
                        doc.get("route") as? List<Map<String, Double>> ?: return@forEach
                    val geoPoints = mutableListOf<GeoPoint>()
                    for (pointMap in routeArray) {
                        val lat = pointMap["latitude"]
                        val lon = pointMap["longitude"]
                        if (lat != null && lon != null) {
                            geoPoints.add(GeoPoint(lat, lon))
                        }
                    }

                    // Only draw if we have at least 2 points
                    if (geoPoints.size >= 2) {
                        val polyline = Polyline().apply {
                            setPoints(geoPoints)
                            color = android.graphics.Color.RED // or any color
                            width = 10f
                        }
                        mapView.overlays.add(polyline)
                    }
                }
                mapView.invalidate()
            }
    }

    // Handle map taps
    val mapTapOverlay = remember {
        MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                selectedPoint = p

                // Remove previous temporary marker if exists
                selectedMarker?.let {
                    mapView.overlays.remove(it)
                }

                // Create and add a new red temporary marker
                p?.let {
                    selectedMarker = Marker(mapView).apply {
                        position = it
                        title = "Selected Point"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = ContextCompat.getDrawable(context, android.R.drawable.presence_busy)
                    }
                    mapView.overlays.add(selectedMarker)
                    mapView.invalidate()
                }

                return true
            }


            override fun longPressHelper(p: GeoPoint?): Boolean {
                p?.let {
                    routePoints.add(it)
                    if (routePoints.size > 1) {
                        val polyline = Polyline().apply {
                            setPoints(routePoints)
                            color = android.graphics.Color.RED
                            width = 15f
                        }
                        mapView.overlays.add(polyline)
                        mapView.invalidate()
                    }
                }
                return true
            }

        })

    }

    LaunchedEffect(Unit) {
        mapView.overlays.add(mapTapOverlay)
    }
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column {
                AndroidView(
                    factory = { mapView }, modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            }
            Column {
                val originalDrawable =
                    ContextCompat.getDrawable(context, android.R.drawable.ic_menu_myplaces)

                val width = 150 // desired width
                val height = 150 // desired height

                val bitmap = createBitmap(width, height)
                val canvas = android.graphics.Canvas(bitmap)
                originalDrawable?.setBounds(0, 0, width, height)
                originalDrawable?.draw(canvas)

                val scaledDrawable = bitmap.toDrawable(context.resources)

                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "Search",
                            Modifier
                                .size(60.dp)

                        )
                    }
                    OutlinedTextField(
                        value = searchQuery,
                        shape = CircleShape,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search Location") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth().padding(end=10.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val geocoder = Geocoder(context, Locale.getDefault())
                                try {
                                    val addresses = geocoder.getFromLocationName(searchQuery, 1)
                                    if (!addresses.isNullOrEmpty()) {
                                        val loc = addresses[0]
                                        val point = GeoPoint(loc.latitude, loc.longitude)
                                        mapView.controller.animateTo(point)
                                        val marker = Marker(mapView).apply {
                                            position = point

                                            title = loc.featureName ?: "Search Result"
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                            icon = scaledDrawable
                                        }
                                        mapView.setZoomLevel(20.0)
                                        mapView.overlays.add(marker)
                                        mapView.invalidate()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "No results found",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                } catch (e: IOException) {
                                    Toast.makeText(
                                        context,
                                        "Error: ${e.localizedMessage}",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        )
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick = { showTooltips = !showTooltips },
                    shape = CircleShape,
                    modifier = Modifier.size(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("i", fontSize = 20.sp)
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                    if (showTooltips) {
                        TooltipLabel("Save Road")
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    IconButton(onClick = {
                        val polylineData = routePoints.map {
                            mapOf("latitude" to it.latitude, "longitude" to it.longitude)
                        }
                        if (polylineData != null) {
                            firestore.collection("roadPolylines")
                                .add(mapOf("route" to polylineData))
                        } else {
                            Toast.makeText(
                                context,
                                "Long press on map to select a road coordinates",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        if (userId != null) {
                            val userRef = firestore.collection("users").document(userId)
                            userRef.collection("roadReports").add(mapOf("route" to polylineData))
                        }
                    }, modifier = Modifier.size(55.dp)) {
                        Icon(
                            painter = painterResource(org.osmdroid.library.R.drawable.navto_small),
                            contentDescription = "Save Road",
                            tint = Color.White,
                            modifier = Modifier
                                .size(55.dp)
                                .background(Color.Black)
                                .padding(5.dp)
                                .clip(RoundedCornerShape(50.dp))
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (showTooltips) {
                        TooltipLabel("Report Issue")
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    IconButton(onClick = {
                        if (selectedPoint != null) {
                            cameraLauncher.launch(null)
                        } else {
                            Toast.makeText(
                                context,
                                "Tap map to select a point first",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }, modifier = Modifier.size(55.dp)) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_camera),
                            contentDescription = "Camera",
                            tint = Color.White,
                            modifier = Modifier
                                .size(55.dp)
                                .background(Color.Black)
                                .padding(5.dp)
                                .clip(RoundedCornerShape(50.dp))
                        )
                    }
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

@Composable
fun TooltipLabel(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFF222222),
                shape = RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp
        )
    }
}
