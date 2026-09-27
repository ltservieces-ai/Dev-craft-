package com.example.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProjectEntity
import com.example.ui.theme.*

@Composable
fun PermissionsDashboardDialog(
    project: ProjectEntity,
    onTogglePermission: (String, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IdeDarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = IdeAccentPeach,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("App Permissions Dashboard", color = IdeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Configure custom permissions for ${project.name}. Changes automatically update AndroidManifest.xml and source code.",
                    color = IdeTextSecondary,
                    fontSize = 12.5.sp
                )

                HorizontalDivider(color = IdeDarkOutline, thickness = 1.dp)

                PermissionToggleRow(
                    title = "Push Notifications",
                    subtitle = "POST_NOTIFICATIONS",
                    icon = Icons.Default.Notifications,
                    isChecked = project.notificationsPermission,
                    onCheckedChange = { onTogglePermission("notifications", it) }
                )

                PermissionToggleRow(
                    title = "Files & Media Storage",
                    subtitle = "READ_MEDIA_IMAGES, STORAGE",
                    icon = Icons.Default.Folder,
                    isChecked = project.filesPermission,
                    onCheckedChange = { onTogglePermission("files", it) }
                )

                PermissionToggleRow(
                    title = "Precise Location Access",
                    subtitle = "ACCESS_FINE_LOCATION",
                    icon = Icons.Default.LocationOn,
                    isChecked = project.locationPermission,
                    onCheckedChange = { onTogglePermission("location", it) }
                )

                PermissionToggleRow(
                    title = "Microphone Access",
                    subtitle = "RECORD_AUDIO",
                    icon = Icons.Default.Mic,
                    isChecked = project.microphonePermission,
                    onCheckedChange = { onTogglePermission("mic", it) }
                )

                PermissionToggleRow(
                    title = "Camera Access",
                    subtitle = "CAMERA",
                    icon = Icons.Default.CameraAlt,
                    isChecked = project.cameraPermission,
                    onCheckedChange = { onTogglePermission("camera", it) }
                )

                PermissionToggleRow(
                    title = "Internet & Network Access",
                    subtitle = "INTERNET, ACCESS_NETWORK_STATE",
                    icon = Icons.Default.Language,
                    isChecked = project.internetPermission,
                    onCheckedChange = { onTogglePermission("internet", it) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach)
            ) {
                Text("Done", color = Color(0xFF28180E), fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun PermissionToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(IdeDarkCard)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isChecked) IdeAccentGreen else IdeTextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, color = IdeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(text = subtitle, color = IdeTextTertiary, fontSize = 10.sp)
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = IdeAccentPeach,
                checkedTrackColor = Color(0xFF3F3730),
                uncheckedThumbColor = IdeTextSecondary,
                uncheckedTrackColor = Color(0xFF282631)
            )
        )
    }
}

@Composable
fun AppLogoDialog(
    project: ProjectEntity,
    onSaveLogo: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(project.iconColorHex) }
    var selectedSymbol by remember { mutableStateOf(project.iconSymbol) }
    var customLogoUriString by remember { mutableStateOf(project.customLogoUri) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            customLogoUriString = it.toString()
        }
    }

    val colors = listOf(
        "#38C779", "#DCA683", "#4AC2E2", "#ECC94B", "#F87171", "#818CF8", "#A855F7", "#1E1D24"
    )
    val symbols = listOf("code", "android", "rocket", "music", "game", "terminal", "star")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IdeDarkSurface,
        title = { Text("App Icon & Logo Customizer", color = IdeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Live Icon Preview
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(android.graphics.Color.parseColor(selectedColor)))
                        .border(2.dp, IdeAccentPeach.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (customLogoUriString.isNotBlank()) {
                        AsyncImage(
                            model = customLogoUriString,
                            contentDescription = "Custom App Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        val iconVector = when (selectedSymbol) {
                            "android" -> Icons.Default.Android
                            "rocket" -> Icons.Default.RocketLaunch
                            "music" -> Icons.Default.MusicNote
                            "game" -> Icons.Default.SportsEsports
                            "terminal" -> Icons.Default.Terminal
                            "star" -> Icons.Default.Star
                            else -> Icons.Default.Code
                        }
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Image from Device Files button
                Button(
                    onClick = { filePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = IdeDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IdeAccentPeach),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = IdeAccentPeach,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choose Logo From Files / Gallery", color = IdeAccentPeach, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                if (customLogoUriString.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(onClick = { customLogoUriString = "" }) {
                        Text("Reset to built-in symbol icon", color = Color(0xFFF87171), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Background Color", color = IdeTextSecondary, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colors.forEach { hex ->
                        val isSelected = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .border(if (isSelected) 2.dp else 0.dp, Color.White, CircleShape)
                                .clickable { selectedColor = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Built-in Logo Glyph Symbol", color = IdeTextSecondary, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    symbols.forEach { sym ->
                        val isSelected = selectedSymbol == sym && customLogoUriString.isBlank()
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) IdeAccentPeach.copy(alpha = 0.2f) else IdeDarkCard)
                                .border(1.dp, if (isSelected) IdeAccentPeach else IdeDarkOutline, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedSymbol = sym
                                    customLogoUriString = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (sym) {
                                    "android" -> "🤖"
                                    "rocket" -> "🚀"
                                    "music" -> "🎵"
                                    "game" -> "🎮"
                                    "terminal" -> "💻"
                                    "star" -> "⭐"
                                    else -> "</>"
                                },
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveLogo(selectedColor, selectedSymbol, customLogoUriString) },
                colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach)
            ) {
                Text("Apply Logo", color = Color(0xFF28180E), fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = IdeTextSecondary)
            }
        }
    )
}

@Composable
fun PushUpdatesDialog(
    project: ProjectEntity,
    onPush: (Int, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var newVersionCode by remember { mutableStateOf((project.versionCode + 1).toString()) }
    var newVersionName by remember {
        val parts = project.versionName.split(".")
        val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
        mutableStateOf("${parts.getOrNull(0) ?: "1"}.${minor + 1}.0")
    }
    var changelog by remember { mutableStateOf("- Bug fixes and performance improvements\n- Updated UI for mobile experience") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IdeDarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = IdeAccentPeach)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Push App Update", color = IdeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Release a new update for ${project.name}. Increments app version and records changelog.",
                    color = IdeTextSecondary,
                    fontSize = 12.5.sp
                )

                OutlinedTextField(
                    value = newVersionName,
                    onValueChange = { newVersionName = it },
                    label = { Text("Version Name", color = IdeTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IdeTextPrimary,
                        unfocusedTextColor = IdeTextPrimary,
                        focusedBorderColor = IdeAccentPeach,
                        unfocusedBorderColor = IdeDarkOutline
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = newVersionCode,
                    onValueChange = { newVersionCode = it },
                    label = { Text("Version Code", color = IdeTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IdeTextPrimary,
                        unfocusedTextColor = IdeTextPrimary,
                        focusedBorderColor = IdeAccentPeach,
                        unfocusedBorderColor = IdeDarkOutline
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = changelog,
                    onValueChange = { changelog = it },
                    label = { Text("Release Notes / Changelog", color = IdeTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IdeTextPrimary,
                        unfocusedTextColor = IdeTextPrimary,
                        focusedBorderColor = IdeAccentPeach,
                        unfocusedBorderColor = IdeDarkOutline
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val code = newVersionCode.toIntOrNull() ?: (project.versionCode + 1)
                    onPush(code, newVersionName, changelog)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach)
            ) {
                Text("Publish Update", color = Color(0xFF28180E), fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = IdeTextSecondary)
            }
        }
    )
}

@Composable
fun GitHubDialog(
    project: ProjectEntity,
    currentUsername: String,
    currentToken: String,
    onSaveCredentials: (String, String) -> Unit,
    onCreateRepo: (String, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf(currentUsername.ifBlank { "devcraft-user" }) }
    var token by remember { mutableStateOf(currentToken) }
    var repoName by remember { mutableStateOf(project.name.lowercase().replace(" ", "-")) }
    var isPrivate by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IdeDarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AltRoute, contentDescription = null, tint = IdeAccentPeach)
                Spacer(modifier = Modifier.width(8.dp))
                Text("GitHub Integration", color = IdeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Connect GitHub to push ${project.name}, create new remote repository, and clone code.",
                    color = IdeTextSecondary,
                    fontSize = 12.5.sp
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("GitHub Username", color = IdeTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IdeTextPrimary,
                        unfocusedTextColor = IdeTextPrimary,
                        focusedBorderColor = IdeAccentPeach,
                        unfocusedBorderColor = IdeDarkOutline
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Personal Access Token (PAT)", color = IdeTextSecondary) },
                    placeholder = { Text("ghp_xxxx", color = IdeTextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IdeTextPrimary,
                        unfocusedTextColor = IdeTextPrimary,
                        focusedBorderColor = IdeAccentPeach,
                        unfocusedBorderColor = IdeDarkOutline
                    ),
                    singleLine = true
                )

                HorizontalDivider(color = IdeDarkOutline, thickness = 1.dp)

                Text("Create New Repository", color = IdeTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                OutlinedTextField(
                    value = repoName,
                    onValueChange = { repoName = it },
                    label = { Text("Repository Name", color = IdeTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IdeTextPrimary,
                        unfocusedTextColor = IdeTextPrimary,
                        focusedBorderColor = IdeAccentPeach,
                        unfocusedBorderColor = IdeDarkOutline
                    ),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Private Repository", color = IdeTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = IdeAccentPeach)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveCredentials(token, username)
                    onCreateRepo(repoName, isPrivate)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach)
            ) {
                Text("Create & Push", color = Color(0xFF28180E), fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = IdeTextSecondary)
            }
        }
    )
}

@Composable
fun CloudSyncDialog(
    status: String,
    onSyncNow: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IdeDarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudSync, contentDescription = null, tint = IdeAccentPeach)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cloud Backup & Sync", color = IdeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Sync all project sources, manifests, and build configs to secure cloud backup.",
                    color = IdeTextSecondary,
                    fontSize = 13.sp
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = IdeDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IdeAccentGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = status, color = IdeTextPrimary, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSyncNow,
                colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach)
            ) {
                Text("Sync Now", color = Color(0xFF28180E), fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = IdeTextSecondary)
            }
        }
    )
}

@Composable
fun PreferencesDialog(
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    fontSize: Int,
    onFontSizeChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IdeDarkSurface,
        title = { Text("Preferences", color = IdeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Theme Mode", color = IdeTextPrimary, fontSize = 14.sp)
                        Text("Optimized for nighttime coding", color = IdeTextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleDarkTheme() },
                        colors = SwitchDefaults.colors(checkedThumbColor = IdeAccentPeach)
                    )
                }

                HorizontalDivider(color = IdeDarkOutline, thickness = 1.dp)

                Column {
                    Text("Editor Font Size: ${fontSize}sp", color = IdeTextPrimary, fontSize = 14.sp)
                    Slider(
                        value = fontSize.toFloat(),
                        onValueChange = { onFontSizeChange(it.toInt()) },
                        valueRange = 10f..22f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = IdeAccentPeach,
                            activeTrackColor = IdeAccentPeach
                        )
                    )
                }

                HorizontalDivider(color = IdeDarkOutline, thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Auto-Save Files", color = IdeTextPrimary, fontSize = 14.sp)
                        Text("Save changes continuously", color = IdeTextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        colors = SwitchDefaults.colors(checkedThumbColor = IdeAccentPeach)
                    )
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
