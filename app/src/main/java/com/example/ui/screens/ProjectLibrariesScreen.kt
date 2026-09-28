package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.theme.*

data class ProjectLibraryItem(
    val id: String,
    val name: String,
    val category: String,
    val dependency: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    var isEnabled: Boolean = false,
    var configKey: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectLibrariesScreen(
    project: ProjectEntity,
    onBack: () -> Unit,
    onAddDependencyToGradle: (String) -> Unit = {}
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var customDepInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val libraries = remember {
        mutableStateListOf(
            ProjectLibraryItem(
                id = "audio_fetcher",
                name = "Songs & Music Fetcher",
                category = "Media",
                dependency = "implementation(\"com.devcraft.audio:audiokit:2.4.0\")",
                description = "Scans local phone audio files (MediaStore), playlist manager, background audio service",
                icon = Icons.Default.MusicNote,
                isEnabled = true
            ),
            ProjectLibraryItem(
                id = "youtube_sdk",
                name = "YouTube & Web Player",
                category = "Media",
                dependency = "implementation(\"com.devcraft.youtube:player-sdk:3.1.0\")",
                description = "YouTube IFrame player embed, stream extractor, video controls & playlist",
                icon = Icons.Default.PlayCircle,
                isEnabled = true
            ),
            ProjectLibraryItem(
                id = "firebase_auth",
                name = "Firebase Authentication & DB",
                category = "Cloud",
                dependency = "implementation(\"com.google.firebase:firebase-auth-ktx:23.1.0\")\nimplementation(\"com.google.firebase:firebase-firestore-ktx:25.1.0\")",
                description = "Google sign-in, Email/Password auth, and Cloud Firestore realtime database",
                icon = Icons.Default.Cloud,
                isEnabled = false
            ),
            ProjectLibraryItem(
                id = "admob",
                name = "Google AdMob",
                category = "Monetization",
                dependency = "implementation(\"com.google.android.gms:play-services-ads:23.6.0\")",
                description = "Display banner ads, interstitial full-screen ads, and rewarded video ads",
                icon = Icons.Default.MonetizationOn,
                isEnabled = false,
                configKey = "ca-app-pub-3940256099942544~3347511713"
            ),
            ProjectLibraryItem(
                id = "google_maps",
                name = "Google Maps SDK",
                category = "Location",
                dependency = "implementation(\"com.google.android.gms:play-services-maps:19.0.0\")\nimplementation(\"com.google.maps.android:maps-compose:6.2.1\")",
                description = "Interactive 3D maps, user geolocation markers, routing & street view",
                icon = Icons.Default.Map,
                isEnabled = false,
                configKey = "AIzaSyDevCraftDemoMapsKey"
            ),
            ProjectLibraryItem(
                id = "razorpay",
                name = "Razorpay Payment Gateway",
                category = "Monetization",
                dependency = "implementation(\"com.razorpay:checkout:1.6.40\")",
                description = "Accept UPI (Google Pay, PhonePe, Paytm), credit cards, debit cards, net banking",
                icon = Icons.Default.Payment,
                isEnabled = false,
                configKey = "rzp_test_devcraft"
            ),
            ProjectLibraryItem(
                id = "retrofit",
                name = "Retrofit & REST Networking",
                category = "Network",
                dependency = "implementation(\"com.squareup.retrofit2:retrofit:2.11.0\")\nimplementation(\"com.squareup.okhttp3:okhttp:4.12.0\")",
                description = "Fast HTTP client, REST API calling, JSON serialization & deserialization",
                icon = Icons.Default.Http,
                isEnabled = true
            )
        )
    }

    Scaffold(
        containerColor = IdeDarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Project Settings & Libraries", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text(project.name, color = IdeAccentPeach, fontSize = 11.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IdeDarkSurfaceVariant)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Add Custom Library Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D24)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IdeAccentPeach.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddBox, contentDescription = null, tint = IdeAccentPeach, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Custom Library / Dependency", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customDepInput,
                            onValueChange = { customDepInput = it },
                            placeholder = { Text("e.g. implementation(\"io.coil-kt:coil:2.6.0\")", fontSize = 12.sp, color = Color(0xFF71717A)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF121214),
                                unfocusedContainerColor = Color(0xFF121214),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (customDepInput.isNotBlank()) {
                                    val dep = customDepInput.trim()
                                    libraries.add(
                                        ProjectLibraryItem(
                                            id = "custom_${libraries.size}",
                                            name = dep.substringBefore(':').removePrefix("implementation(\"").removePrefix("implementation '"),
                                            category = "Custom",
                                            dependency = dep,
                                            description = "Custom user-added library dependency",
                                            icon = Icons.Default.Extension,
                                            isEnabled = true
                                        )
                                    )
                                    onAddDependencyToGradle(dep)
                                    Toast.makeText(context, "Added to build.gradle successfully!", Toast.LENGTH_SHORT).show()
                                    customDepInput = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Add to build.gradle", color = Color(0xFF28180E), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Pre-loaded Frameworks & SDKs",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Library Cards
            items(libraries) { lib ->
                var enabled by remember { mutableStateOf(lib.isEnabled) }
                var configVal by remember { mutableStateOf(lib.configKey) }
                var showConfig by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18171E)),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (enabled) Color(0xFF38C779).copy(alpha = 0.6f) else Color(0xFF27272A)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (enabled) Color(0xFF38C779).copy(alpha = 0.2f) else Color(0xFF27272A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = lib.icon,
                                        contentDescription = null,
                                        tint = if (enabled) Color(0xFF38C779) else Color(0xFFA19EAA),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(lib.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(lib.category, color = IdeAccentPeach, fontSize = 11.sp)
                                }
                            }

                            Switch(
                                checked = enabled,
                                onCheckedChange = {
                                    enabled = it
                                    lib.isEnabled = it
                                    if (it) {
                                        onAddDependencyToGradle(lib.dependency)
                                        Toast.makeText(context, "${lib.name} enabled in project!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "${lib.name} disabled", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF38C779),
                                    checkedTrackColor = Color(0xFF38C779).copy(alpha = 0.3f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(lib.description, color = Color(0xFFA19EAA), fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        // Gradle Snippet
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF100F14))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = lib.dependency,
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (lib.configKey.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { showConfig = !showConfig },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = IdeAccentPeach, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (showConfig) "Hide API Keys" else "Configure API Keys / IDs", fontSize = 11.sp, color = IdeAccentPeach)
                                }
                            }

                            if (showConfig) {
                                OutlinedTextField(
                                    value = configVal,
                                    onValueChange = {
                                        configVal = it
                                        lib.configKey = it
                                    },
                                    label = { Text("API Key / App ID", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Developer support card at bottom
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A151C)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("DevCraft IDE is 100% Free", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Support Developer directly via UPI: 8791738300@fam", color = Color(0xFFF87171), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
