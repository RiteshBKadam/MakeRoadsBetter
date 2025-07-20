//package com.example.makeroadsbetter
//
//import android.app.Activity
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import com.example.makeroadsbetter.viewmodel.LocationViewModel
//import org.osmdroid.tileprovider.tilesource.TileSourceFactory
//import org.osmdroid.util.GeoPoint
//import org.osmdroid.views.MapView
//import org.osmdroid.config.Configuration
//import org.osmdroid.events.MapEventsReceiver
//import org.osmdroid.views.overlay.MapEventsOverlay
//import org.osmdroid.views.overlay.Marker
//import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
//import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        // Initialize osmdroid configuration
//        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))
//
//        setContent {
//            App()
//        }
//    }
//}
//
//@Composable
//fun App() {
//    RequestPermissions {
//        val viewmodel=LocationViewModel()
//        OsmMapView(viewmodel)
//    }
//}
//
//@Composable
//fun RequestPermissions(
//    content: @Composable () -> Unit
//) {
//    val context = LocalContext.current
//
//    // List of required permissions
//    val requiredPermissions = arrayOf(
//        android.Manifest.permission.ACCESS_FINE_LOCATION,
//        android.Manifest.permission.ACCESS_COARSE_LOCATION,
//        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        android.Manifest.permission.READ_EXTERNAL_STORAGE
//    ).filter {
//        // Filter out permissions not needed for current SDK level
//        if (it == android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
//            Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
//        } else if (it == android.Manifest.permission.READ_EXTERNAL_STORAGE) {
//            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q
//        } else {
//            true
//        }
//    }.toTypedArray()
//
//    val allPermissionsGranted = requiredPermissions.all {
//        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    // Launcher for requesting permissions
//    val launcher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { /* Handle results if needed */ }
//
//    // Request permissions if not granted
//    LaunchedEffect(Unit) {
//        if (!allPermissionsGranted) {
//            launcher.launch(requiredPermissions)
//        }
//    }
//
//    if (allPermissionsGranted) {
//        content()
//    } else {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Text("Map requires location permissions to work properly")
//        }
//    }
//}
//
//@Composable
//fun OsmMapView(viewModel: LocationViewModel) {
//    val context = LocalContext.current
//    val location = viewModel.location.value
//    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
//
//    // Initialize the map view
//    val mapView = remember {
//        MapView(context).apply {
//            setTileSource(TileSourceFactory.MAPNIK)
//            setMultiTouchControls(true)
//            controller.setZoom(20.0)
//        }
//    }
//
//    // Update map center when location changes
//    LaunchedEffect(location) {
//        if (location != null) {
//            val geoPoint = GeoPoint(location.latitude, location.longitude)
//            mapView.controller.animateTo(geoPoint)
//
//            // Optionally add a marker at current location
//            val marker = Marker(mapView).apply {
//                position = geoPoint
//                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//                title = "Current Location"
//            }
//
//            val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
//                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
//                    p?.let {
//                        selectedPoint = it
//                        Toast.makeText(context, "Lat: ${it.latitude}, Lon: ${it.longitude}", Toast.LENGTH_SHORT).show()
//                        Log.d("THISSSSSSSSSSSSSS","Lat: ${it.latitude}, Lon: ${it.longitude}")
//                    }
//                    return true
//                }
//
//                override fun longPressHelper(p: GeoPoint?): Boolean {
//                    return true
//                }
//            })
//            mapView.overlays.add(marker)
//            mapView.overlays.add(mapEventsOverlay)
//        }
//        mapView
//    }
//
//
//    // Add location overlay for real-time position tracking
//    DisposableEffect(Unit) {
//        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
//            enableMyLocation()
//            enableFollowLocation()
//        }
//        mapView.overlays.add(locationOverlay)
//
//        onDispose {
//            locationOverlay.disableMyLocation()
//        }
//    }
//
//    AndroidView(
//        factory = { mapView },
//        modifier = Modifier.fillMaxSize()
//    )
//}