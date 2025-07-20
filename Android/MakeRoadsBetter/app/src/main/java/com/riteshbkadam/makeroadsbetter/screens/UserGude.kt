

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserGuidePopup(onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .heightIn(min = 300.dp, max = 600.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "How to Use",
                    fontSize = 21.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,

                )
                Spacer(modifier = Modifier.height(12.dp))

                GuideItem("📍 Live Map Interaction", listOf(
                    "The app opens with a live map showing your current location.",
                    "You can zoom and move the map to explore nearby areas.",
                    "Red lines indicate reported bad road segments by users."
                ))

                GuideItem("👆 Tap to Select a Point", listOf(
                    "Single-tap on the map to select a location where there's a road issue.",
                    "This point will be saved and used for reporting."
                ))

                GuideItem("📷 Report a Road Issue", listOf(
                    "After selecting a point, press the 📷 \"Snap\" button to open your camera.",
                    "Capture a photo of the bad road or blockage.",
                    "Your report (photo + location) is saved in the system and appears on the map."
                ))

                GuideItem("🧭 Draw & Save Road Route", listOf(
                    "Long-press the map to add points and draw a route (e.g., damaged road stretch).",
                    "Once your path is complete, press \"Save\" to upload the full route."
                ))

                GuideItem("ℹ️ Info Button", listOf(
                    "Tap the “i” icon to see helpful tooltips near each button.",
                    "Tooltips explain what each action (like Snap or Save) does."
                ))

                GuideItem("🧾 View All Reports", listOf(
                    "All users’ road reports are displayed on the map as markers.",
                    "Tap a marker to view the attached photo."
                ))

                GuideItem("🙋‍♂️ My Contributions", listOf(
                    "Go to \"My Reports\" section (User Contributions Map).",
                    "See all the roads you’ve reported, marked with special tags.",
                    "Tap to view images you submitted earlier."
                ))

                GuideItem("🔐 Login & Logout", listOf(
                    "Secure login with email and password.",
                    "Use the Logout button to safely sign out and return to the login screen."
                ))

                GuideItem("📜 Additional Notes", listOf(
                    "Location and camera permissions are required.",
                    "Only logged-in users can report road issues.",
                    "All reports are saved in real time and visible to other users."
                ))

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Close", color = Color.White, fontSize = 17.sp) // Increased by 1sp
                }
            }
        }
    }
}


@Composable
fun GuideItem(title: String, points: List<String>) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = title,
            fontSize = 19.sp, // Increased by 1sp
            color = Color(0xFF222222)
        )
        Spacer(modifier = Modifier.height(4.dp))
        points.forEach {
            Text(
                text = "• $it",
                fontSize = 15.sp, // Increased by 1sp
                color = Color.DarkGray
            )
        }
    }
}

