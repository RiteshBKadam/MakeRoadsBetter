######################################
# Make Roads Better - ProGuard Rules #
######################################

# --- AndroidX and Jetpack Compose ---
-keep class androidx.compose.** { *; }
-keep class androidx.activity.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

# --- Firebase ---
# Firebase Core
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Prevent stripping of Firebase init code
-keepnames class com.google.firebase.components.** { *; }

# --- Gson / Moshi (if used) ---
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Keep model classes for Gson
-keep class your.package.name.model.** { *; }

# --- Retrofit (if used) ---
-keep class retrofit2.** { *; }

-keepinterfaces class * {
    @retrofit2.http.* <methods>;
}
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# --- OSMDroid ---
-keep class org.osmdroid.** { *; }

# --- Kotlin & Coroutines ---
-dontwarn kotlin.**
-keep class kotlinx.coroutines.** { *; }



# --- Serialization and Reflection ---
-keepattributes *Annotation*
-keepclassmembers class * {
    public <init>(...);
}


# --- Entry Points / Main Activity ---
-keep class com.riteshbkadam.makeroadsbetter.MainActivity { *; }

# --- Logging (Optional) ---
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# --- Google Maps / Location (Optional) ---
-keep class com.google.android.gms.maps.** { *; }
-keep class com.google.android.gms.location.** { *; }

# --- Prevent Obfuscation of Constants and XML References ---
-keepclassmembers class * {
    public static <fields>;
}

# --- Prevent stripping your data classes (safe) ---
-keep class your.package.name.model.** { *; }
