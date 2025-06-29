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
package com.example.makeroadsbetter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.makeroadsbetter.util.LocationUtils
import com.example.makeroadsbetter.viewmodel.LocationViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize osmdroid configuration
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))

        setContent {
            App()
        }
    }
}

fun checkPlayServices(context: Context): Boolean {
    val apiAvailability = GoogleApiAvailability.getInstance()
    val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
    if (resultCode != ConnectionResult.SUCCESS) {
        if (apiAvailability.isUserResolvableError(resultCode)) {
            apiAvailability.getErrorDialog(context as Activity, resultCode, 9000)?.show()
        } else {
            Toast.makeText(context,"error",Toast.LENGTH_SHORT).show()
        }
        return false
    }
    return true
}
@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun App() {

    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }
        composable("map") {
            val viewModel = LocationViewModel()
            OsmMapView(viewModel, navController)
        }
        composable("images") {
            ImageListScreen(navController)
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun HomeScreen(navController: NavController) {
    val context= LocalContext.current
    checkPlayServices(context)
    RequestPermissions {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate("map") }) {
                Text("Open Map")
            }
            Button(onClick = { navController.navigate("images") }) {
                Text("View Stored Images")
            }
        }
    }
}


@Composable
fun RequestPermissions(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // List of required permissions
    val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ).filter {
        // Filter out permissions not needed for current SDK level
        if (it == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
        } else if (it == Manifest.permission.READ_EXTERNAL_STORAGE) {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q
        } else {
            true
        }
    }.toTypedArray()

    val allPermissionsGranted = requiredPermissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // Launcher for requesting permissions
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* Handle results if needed */ }

    // Request permissions if not granted
    LaunchedEffect(Unit) {
        if (!allPermissionsGranted) {
            launcher.launch(requiredPermissions)
        }
    }

    if (allPermissionsGranted) {
        content()
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Map requires location permissions to work properly")
        }
    }
}
@Composable
fun OsmMapView(viewModel: LocationViewModel, navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    val locationUtils=LocationUtils(context)
    locationUtils.requestLocationUpdates(viewModel)
    val location = viewModel.location.value
    var currentMarker: Marker? by remember { mutableStateOf(null) }
    var marker: Marker? by remember { mutableStateOf(null) }
    val images = remember { mutableStateListOf<Bitmap>() }
    val coordinates = remember { mutableStateListOf<GeoPoint>() }
    var showPopup by remember { mutableStateOf(false) }
    var selectRoad by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    var routesCordinates= emptyList<GeoPoint>()
    var launchCamera by remember { mutableStateOf(false) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(20.0)
        }
    }
    if (showPopup) {
        RoadConditionDataPopPopUp(onDismiss = { showPopup = false },selectedImage,context)
    }
    if (selectRoad) {
       routesCordinates=SelectRoad(
            mapView,firestore
        )
    }
    var shouldLaunchCamera by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val bitmap = it.data?.extras?.get("data") as? Bitmap
            bitmap?.let { image ->
                selectedPoint?.let { point ->
                    // ✅ Add marker with photo at selected point
                    mapView?.let { map ->
                        val marker = Marker(map).apply {
                            position = point
                            title = "Photo Marker"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setOnMarkerClickListener { _, _ ->
                                Toast.makeText(
                                    context,
                                    "Marker at ${point.latitude}, ${point.longitude}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                true
                            }
                        }

                        map.overlays.add(marker)
                        map.invalidate()
                    }
                }
            }
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val base64Image = bitmapToBase64(it)

            val point = selectedPoint
            if (point != null) {
                val data = hashMapOf(
                    "latitude" to point.latitude,
                    "longitude" to point.longitude,
                    "image" to base64Image
                )

                firestore.collection("roadConditionDataMap")
                    .add(data)
                    .addOnSuccessListener { Log.d("FIRESTORE", "Image stored successfully!") }
                    .addOnFailureListener { e -> Log.e("FIRESTORE", "Error storing image", e) }

                // ✅ Add marker to map
                mapView?.let { map ->
                    val marker = Marker(map).apply {
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Road Issue"
                        setOnMarkerClickListener { _, _ ->
                            Toast.makeText(context, "Image stored at this location", Toast.LENGTH_SHORT).show()
                            true
                        }
                    }
                    map.overlays.add(marker)
                    map.invalidate()
                }
            }
        }
    }
    if (launchCamera) {
        imageLauncher.launch(null)
        launchCamera = false
    }
    LaunchedEffect(location,images) {
        if (shouldLaunchCamera) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
            shouldLaunchCamera = false
        }

        if (location != null) {
            val geoPoint = location?.let { it1 -> GeoPoint(it1.latitude, location.longitude) }
            mapView.controller.animateTo(geoPoint)

            currentMarker?.let { mapView.overlays.remove(it) }

            val marker = Marker(mapView).apply {
                position = geoPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Selected Location"
            }
            mapView.overlays.add(marker)
            currentMarker = marker
            mapView.invalidate()


            firestore.collection("roadConditionDataMap")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val latitude = document.getDouble("latitude") ?: 0.0
                        val longitude = document.getDouble("longitude") ?: 0.0
                        val base64Image = document.getString("image") ?: ""

                        val bitmap = base64ToBitmap(base64Image)
                        if (bitmap != null) {
                            coordinates.add(GeoPoint(latitude, longitude))
                            images.add(bitmap)
                            val marker2 = Marker(mapView).apply {

                                position = GeoPoint(latitude, longitude)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Selected Location"
                                setOnMarkerClickListener { m, _ ->
                                    selectedImage = bitmap
                                    showPopup = true
                                    true
                                }
                            }
                            mapView.overlays.add(marker2)
                        } else {
                            Log.e("FIRESTORE", "Failed to decode Base64 image")
                        }
                    }

                }
                .addOnFailureListener { e -> Log.e("FIRESTORE", "Error fetching images", e) }

            val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    p?.let {
                        selectedPoint = it
                        Toast.makeText(
                            context,
                            "Lat: ${it.latitude}, Lon: ${it.longitude}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("THISSSSSSSSSSSSSS", "Lat: ${it.latitude}, Lon: ${it.longitude}")
//                        shouldLaunchCamera = true // ✅ trigger camera safely
                        launchCamera=true
                    }

                    return true
                }

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    return true
                }
            })
            mapView.overlays.add(mapEventsOverlay)

        }
        mapView
    }

    Column {
        AndroidView(factory = { mapView }, modifier = Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Home")
        }
        Button(onClick = {selectRoad=true}) { Text("Start selecting road")}
        Button(
            onClick = { storePolylineInFirestore(routesCordinates, firestore) },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Done Adding Points")
        }
    }
}

@Composable
fun SelectRoad(mapView: MapView, firestore: FirebaseFirestore): List<GeoPoint> {
    var routePoints = remember { mutableStateListOf<GeoPoint>() }

    val mapEventsOverlay = remember {
        MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    routePoints.add(it)
                    if (routePoints.size > 1) {
                        drawPolyline(routePoints.toList(), mapView,firestore)
                    }
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        })
    }

    LaunchedEffect(Unit) {
        mapView.overlays.add(mapEventsOverlay)
    }
    return routePoints.toList()
}

private fun storePolylineInFirestore(routePoints: List<GeoPoint>, firestore: FirebaseFirestore) {
    val coordinatesList = routePoints.map { point ->
        mapOf("latitude" to point.latitude, "longitude" to point.longitude)
    }

    firestore.collection("roadPolylines")
        .add(mapOf("route" to coordinatesList))
        .addOnSuccessListener { Log.d("Firestore", "Polyline saved successfully!") }
        .addOnFailureListener { e -> Log.e("Firestore", "Error saving polyline", e) }
}


private fun drawPolyline(routePoints: List<GeoPoint>, map: MapView,firestore: FirebaseFirestore) {
    Handler(Looper.getMainLooper()).post {

        val polyline = Polyline().apply {
            setPoints(routePoints)
            color = android.graphics.Color.BLUE // Line color
            width = 8f // Line thickness
        }
        map.overlays.add(polyline)
        map.invalidate()
        val coordinatesList = routePoints.map { point ->
            mapOf("latitude" to point.latitude, "longitude" to point.longitude)
        }
        Log.d("Points",coordinatesList.toString())
    }
}



@Composable
fun RoadConditionDataPopPopUp(onDismiss: () -> Unit,image:Bitmap?,context: Context) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.background(Color.White)
                .height(350.dp)
                .width(300.dp)
        ) {
            image?.let {
                Toast.makeText(context,"thisss",Toast.LENGTH_SHORT).show()
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Road Condition Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
    }
}

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


    @Composable
    fun ImageListScreen(navController: NavController) {
        val firestore = FirebaseFirestore.getInstance()
        val images = remember { mutableStateListOf<Bitmap>() }
        val coordinates = remember { mutableStateListOf<GeoPoint>() }

        LaunchedEffect(Unit) {
            firestore.collection("roadConditionDataMap")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val latitude = document.getDouble("latitude") ?: 0.0
                        val longitude = document.getDouble("longitude") ?: 0.0
                        val base64Image = document.getString("image") ?: ""

                        val bitmap = base64ToBitmap(base64Image)
                        if (bitmap != null) {
                            coordinates.add(GeoPoint(latitude, longitude))
                            images.add(bitmap)
                        } else {
                            Log.e("FIRESTORE", "Failed to decode Base64 image")
                        }
                    }
                }
                .addOnFailureListener { e -> Log.e("FIRESTORE", "Error fetching images", e) }
        }

        Column {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(images.size) { index ->
                    Column {
                        Text("Lat: ${coordinates[index].latitude}, Lon: ${coordinates[index].longitude}")
                        Image(
                            bitmap = images[index].asImageBitmap(),
                            contentDescription = "Road Condition Image",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Home")
            }

        }
    }

    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
