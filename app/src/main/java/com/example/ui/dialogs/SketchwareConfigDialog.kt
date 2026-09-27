package com.example.ui.dialogs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.theme.*

enum class SketchwareSubPage {
    MENU,
    LIBRARY_MANAGER,
    APPCOMPAT,
    FIREBASE,
    ADMOB,
    GOOGLE_MAPS,
    VIEW_MANAGER,
    IMAGE_MANAGER,
    SOUND_MANAGER,
    FONT_MANAGER,
    PERMISSION_MANAGER,
    PROGUARD,
    STRINGFOG,
    COMPONENT_MANAGER,
    CUSTOM_VARIABLES,
    DIRECT_INJECTOR,
    NATIVE_LIBRARIES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SketchwareConfigDialog(
    project: ProjectEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentPage by remember { mutableStateOf(SketchwareSubPage.MENU) }

    // Settings State matching Screenshots 5, 6, 7, 8
    var appCompatEnabled by remember { mutableStateOf(true) }
    var firebaseEnabled by remember { mutableStateOf(false) }
    var firebaseDbUrl by remember { mutableStateOf("sk-pro-default-rtdb.firebaseio.com") }
    var firebaseAppId by remember { mutableStateOf("1:250552518990:android:devcraft") }
    var firebaseApiKey by remember { mutableStateOf("") }
    var firebaseStorageUrl by remember { mutableStateOf("devcraft.appspot.com") }

    var admobEnabled by remember { mutableStateOf(false) }
    var admobBannerId by remember { mutableStateOf("ca-app-pub-3940256099942544/6300978111") }

    var mapsEnabled by remember { mutableStateOf(false) }
    var mapsApiKey by remember { mutableStateOf("") }

    var proGuardEnabled by remember { mutableStateOf(true) }
    var stringFogEnabled by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1D24),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentPage != SketchwareSubPage.MENU) {
                    IconButton(onClick = {
                        currentPage = if (currentPage in listOf(SketchwareSubPage.APPCOMPAT, SketchwareSubPage.FIREBASE, SketchwareSubPage.ADMOB, SketchwareSubPage.GOOGLE_MAPS)) {
                            SketchwareSubPage.LIBRARY_MANAGER
                        } else {
                            SketchwareSubPage.MENU
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = IdeTextPrimary)
                    }
                }
                Text(
                    text = when (currentPage) {
                        SketchwareSubPage.MENU -> "Configuration"
                        SketchwareSubPage.LIBRARY_MANAGER -> "Library Manager"
                        SketchwareSubPage.APPCOMPAT -> "AppCompat and Design"
                        SketchwareSubPage.FIREBASE -> "Firebase Manager"
                        SketchwareSubPage.ADMOB -> "AdMob Manager"
                        SketchwareSubPage.GOOGLE_MAPS -> "GoogleMap Settings"
                        SketchwareSubPage.VIEW_MANAGER -> "View Manager"
                        SketchwareSubPage.IMAGE_MANAGER -> "Image Manager"
                        SketchwareSubPage.SOUND_MANAGER -> "Sound Manager"
                        SketchwareSubPage.FONT_MANAGER -> "Font Manager"
                        SketchwareSubPage.PERMISSION_MANAGER -> "Permission Manager"
                        SketchwareSubPage.PROGUARD -> "ProGuard Obfuscation"
                        SketchwareSubPage.STRINGFOG -> "StringFog Encryption"
                        SketchwareSubPage.COMPONENT_MANAGER -> "Component Manager"
                        SketchwareSubPage.CUSTOM_VARIABLES -> "Variable & Logic Blocks"
                        SketchwareSubPage.DIRECT_INJECTOR -> "Direct Code & Manifest Injector"
                        SketchwareSubPage.NATIVE_LIBRARIES -> "Native NDK Libraries (.so)"
                    },
                    color = IdeTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when (currentPage) {
                    SketchwareSubPage.MENU -> {
                        // Configuration Menu matching Screenshots 10, 11, 12
                        ConfigMenuItem("Library", "Component settings (Firebase, AdMob, Maps)", Icons.Default.Dashboard, IdeAccentPeach) {
                            currentPage = SketchwareSubPage.LIBRARY_MANAGER
                        }
                        ConfigMenuItem("View", "Manage multiple screens & activities", Icons.Default.Layers, IdeAccentCyan) {
                            currentPage = SketchwareSubPage.VIEW_MANAGER
                        }
                        ConfigMenuItem("Image", "Import photos and icons from files", Icons.Default.Image, IdeAccentGreen) {
                            currentPage = SketchwareSubPage.IMAGE_MANAGER
                        }
                        ConfigMenuItem("Sound", "Import music and sound effects", Icons.Default.Equalizer, IdeAccentYellow) {
                            currentPage = SketchwareSubPage.SOUND_MANAGER
                        }
                        ConfigMenuItem("Font", "Import different fonts (Roboto, Poppins)", Icons.Default.TextFields, IdeTextPrimary) {
                            currentPage = SketchwareSubPage.FONT_MANAGER
                        }
                        ConfigMenuItem("Permission", "Add custom permissions", Icons.Default.Security, Color(0xFFC084FC)) {
                            currentPage = SketchwareSubPage.PERMISSION_MANAGER
                        }
                        ConfigMenuItem("ProGuard", "Minimize/obfuscate your app code", Icons.Default.Shield, IdeTextSecondary) {
                            currentPage = SketchwareSubPage.PROGUARD
                        }
                        ConfigMenuItem("StringFog", "Encrypt strings in your app", Icons.Default.Lock, IdeAccentPeach) {
                            currentPage = SketchwareSubPage.STRINGFOG
                        }
                        ConfigMenuItem("Component", "Intent, SharedPref, MediaPlayer, Timer, Sensors", Icons.Default.Extension, IdeAccentGreen) {
                            currentPage = SketchwareSubPage.COMPONENT_MANAGER
                        }
                        ConfigMenuItem("Variables & Logic", "Create Boolean, Number, String, Map blocks", Icons.Default.DataObject, IdeAccentCyan) {
                            currentPage = SketchwareSubPage.CUSTOM_VARIABLES
                        }
                        ConfigMenuItem("Direct Injector", "Inject raw Java/Kotlin & Manifest XML tags", Icons.Default.Code, IdeAccentYellow) {
                            currentPage = SketchwareSubPage.DIRECT_INJECTOR
                        }
                        ConfigMenuItem("Native Libraries", "Configure arm64-v8a, armeabi-v7a NDK .so", Icons.Default.Memory, Color(0xFFC084FC)) {
                            currentPage = SketchwareSubPage.NATIVE_LIBRARIES
                        }
                    }

                    SketchwareSubPage.LIBRARY_MANAGER -> {
                        // Library Manager matching Screenshot 5
                        LibraryRow("AppCompat and Design", "Drawer Layout, Floating Action Button", appCompatEnabled, Icons.Default.Build) {
                            currentPage = SketchwareSubPage.APPCOMPAT
                        }
                        LibraryRow("Firebase", "Use Firebase Database and Authentication", firebaseEnabled, Icons.Default.Whatshot) {
                            currentPage = SketchwareSubPage.FIREBASE
                        }
                        LibraryRow("AdMob", "Use Google AdMob", admobEnabled, Icons.Default.Campaign) {
                            currentPage = SketchwareSubPage.ADMOB
                        }
                        LibraryRow("GoogleMap", "Use Google Map", mapsEnabled, Icons.Default.Map) {
                            currentPage = SketchwareSubPage.GOOGLE_MAPS
                        }
                    }

                    SketchwareSubPage.APPCOMPAT -> {
                        // Matching Screenshot 7
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enabled", color = if (appCompatEnabled) IdeAccentGreen else IdeTextSecondary, fontSize = 15.sp)
                            Switch(checked = appCompatEnabled, onCheckedChange = { appCompatEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = IdeAccentPeach))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Activating App Compat library will let you add Drawer Layout and Floating Action Button to the Activities.\n\nIncluding the library may slow down the compilation time on certain devices.",
                            color = IdeTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    SketchwareSubPage.FIREBASE -> {
                        // Matching Screenshot 8
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enabled", color = if (firebaseEnabled) IdeAccentGreen else IdeTextSecondary, fontSize = 15.sp)
                            Switch(checked = firebaseEnabled, onCheckedChange = { firebaseEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = IdeAccentPeach))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = firebaseDbUrl,
                            onValueChange = { firebaseDbUrl = it },
                            label = { Text("Full Database Url (e.g. sk-pro-default-rtdb.firebaseio.com)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = firebaseAppId,
                            onValueChange = { firebaseAppId = it },
                            label = { Text("App ID") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = firebaseApiKey,
                            onValueChange = { firebaseApiKey = it },
                            label = { Text("API Key") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = firebaseStorageUrl,
                            onValueChange = { firebaseStorageUrl = it },
                            label = { Text("Storage Bucket URL") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { Toast.makeText(context, "Parsed configuration from google-services.json", Toast.LENGTH_SHORT).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23C16B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Import from google-services.json", color = Color.White)
                        }
                    }

                    SketchwareSubPage.ADMOB -> {
                        // Matching Screenshot 6
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enabled", color = if (admobEnabled) IdeAccentGreen else IdeTextSecondary, fontSize = 15.sp)
                            Switch(checked = admobEnabled, onCheckedChange = { admobEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = IdeAccentPeach))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("First, you need to create Ad Units inside the AdMob console.", color = IdeTextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = admobBannerId,
                            onValueChange = { admobBannerId = it },
                            label = { Text("Banner Ad Unit ID") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    SketchwareSubPage.GOOGLE_MAPS -> {
                        // Matching Screenshot 5
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enabled", color = if (mapsEnabled) IdeAccentGreen else IdeTextSecondary, fontSize = 15.sp)
                            Switch(checked = mapsEnabled, onCheckedChange = { mapsEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = IdeAccentPeach))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Google Map View can be operated. You must enter the API key to operate the map normally.", color = IdeTextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = mapsApiKey,
                            onValueChange = { mapsApiKey = it },
                            label = { Text("API Key") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    SketchwareSubPage.VIEW_MANAGER -> {
                        Text("Project Screens & Activities", color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        listOf("MainActivity.kt (Launcher)", "DetailActivity.kt", "SettingsActivity.kt").forEach { screen ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = IdeDarkCard)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Layers, contentDescription = null, tint = IdeAccentPeach)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(screen, color = IdeTextPrimary, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    SketchwareSubPage.IMAGE_MANAGER -> {
                        Text("Import Photos & Icons", color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { Toast.makeText(context, "Opening device file manager to import image", Toast.LENGTH_SHORT).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFF28180E))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Import Photo from Device Files", color = Color(0xFF28180E))
                        }
                    }

                    SketchwareSubPage.SOUND_MANAGER -> {
                        Text("Import Music & Audio Effects", color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { Toast.makeText(context, "Select sound file from files", Toast.LENGTH_SHORT).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AudioFile, contentDescription = null, tint = Color(0xFF28180E))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Import Audio (.mp3/.wav/.ogg)", color = Color(0xFF28180E))
                        }
                    }

                    SketchwareSubPage.FONT_MANAGER -> {
                        Text("Custom Font Manager", color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        listOf("Roboto-Regular.ttf", "Poppins-SemiBold.ttf", "Montserrat-Bold.ttf").forEach { font ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = IdeDarkCard)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.FontDownload, contentDescription = null, tint = IdeAccentGreen)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(font, color = IdeTextPrimary, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    SketchwareSubPage.PERMISSION_MANAGER -> {
                        Text("Permission Manager", color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("All permissions are synced to AndroidManifest.xml and targetSdk 35.", color = IdeTextSecondary, fontSize = 12.sp)
                    }

                    SketchwareSubPage.PROGUARD -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enable ProGuard / R8 Obfuscation", color = IdeTextPrimary, fontSize = 14.sp)
                            Switch(checked = proGuardEnabled, onCheckedChange = { proGuardEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = IdeAccentPeach))
                        }
                    }

                    SketchwareSubPage.STRINGFOG -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enable StringFog String Encryption", color = IdeTextPrimary, fontSize = 14.sp)
                            Switch(checked = stringFogEnabled, onCheckedChange = { stringFogEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = IdeAccentPeach))
                        }
                    }

                    SketchwareSubPage.COMPONENT_MANAGER -> {
                        Text("Active App Components", color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Add Android framework components to handle intents, audio, timers, and storage.", color = IdeTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        val components = listOf(
                            Triple("Intent (i)", "Switch activities and open external urls", Icons.Default.OpenInNew),
                            Triple("SharedPreferences (sp)", "Key-value persistent storage", Icons.Default.Save),
                            Triple("MediaPlayer (mp)", "Audio player for background music", Icons.Default.MusicNote),
                            Triple("SoundPool (sp)", "Fast low-latency sound effects", Icons.Default.VolumeUp),
                            Triple("Vibrator (vib)", "Device haptic feedback engine", Icons.Default.Vibration),
                            Triple("Timer (t)", "Periodic loop and delayed task execution", Icons.Default.Timer),
                            Triple("Dialog (d)", "Custom Alert Dialog and popup boxes", Icons.Default.ChatBubbleOutline),
                            Triple("Gyroscope / Sensors", "Real-time motion and tilt sensors", Icons.Default.Sensors),
                            Triple("Camera (cam)", "Capture photos and preview stream", Icons.Default.CameraAlt)
                        )

                        components.forEach { (name, desc, icon) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = IdeDarkCard)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(icon, contentDescription = null, tint = IdeAccentPeach, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(name, color = IdeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Text(desc, color = IdeTextSecondary, fontSize = 11.sp)
                                    }
                                    TextButton(onClick = { Toast.makeText(context, "$name component initialized in code", Toast.LENGTH_SHORT).show() }) {
                                        Text("Add", color = IdeAccentGreen, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    SketchwareSubPage.CUSTOM_VARIABLES -> {
                        Text("Variables & Data Structures", color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Manage reactive typed variables for your activity logic blocks.", color = IdeTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        val varTypes = listOf(
                            Pair("Boolean (true/false)", "var isRunning: Boolean = false"),
                            Pair("Number / Integer", "var counter: Int = 0"),
                            Pair("String Text", "var userTitle: String = \"Hello\""),
                            Pair("List<String>", "val itemList = ArrayList<String>()"),
                            Pair("Map<String, Any>", "val dataMap = HashMap<String, Any>()")
                        )

                        varTypes.forEach { (type, code) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = IdeDarkCard)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(type, color = IdeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(code, color = IdeAccentPeach, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                                }
                            }
                        }
                    }

                    SketchwareSubPage.DIRECT_INJECTOR -> {
                        Text("Direct Source Code Injector", color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Inject custom Java / Kotlin lines and AndroidManifest xml snippets directly into the build pipeline.", color = IdeTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        var injectCode by remember { mutableStateOf("// Injected block\nandroid:hardwareAccelerated=\"true\"\nandroid:largeHeap=\"true\"") }
                        OutlinedTextField(
                            value = injectCode,
                            onValueChange = { injectCode = it },
                            label = { Text("Direct Code Snippet") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { Toast.makeText(context, "Code injected into project", Toast.LENGTH_SHORT).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach)
                        ) {
                            Text("Inject into Build", color = Color(0xFF28180E))
                        }
                    }

                    SketchwareSubPage.NATIVE_LIBRARIES -> {
                        Text("NDK Native Libraries (.so)", color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Configure CPU Architectures and CMake native toolchain bindings.", color = IdeTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        listOf("arm64-v8a (64-bit ARM)", "armeabi-v7a (32-bit ARM)", "x86_64 (64-bit Emulator)", "x86 (32-bit)").forEach { arch ->
                            var enabled by remember { mutableStateOf(true) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(arch, color = IdeTextPrimary, fontSize = 13.sp)
                                Switch(checked = enabled, onCheckedChange = { enabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = IdeAccentPeach))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach)
            ) {
                Text("Close", color = Color(0xFF28180E))
            }
        }
    )
}

@Composable
fun ConfigMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = IdeDarkCard)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, color = IdeTextSecondary, fontSize = 11.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = IdeTextTertiary)
        }
    }
}

@Composable
fun LibraryRow(
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = IdeDarkCard)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (isEnabled) IdeAccentGreen else IdeTextSecondary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = IdeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = IdeTextSecondary, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .background(if (isEnabled) Color(0xFF1B3828) else Color(0xFF2C2A36), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(if (isEnabled) "ON" else "OFF", color = if (isEnabled) IdeAccentGreen else IdeTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
