package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IdeHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectConfigScreen(
    projectName: String,
    onProjectNameChange: (String) -> Unit,
    packageName: String,
    onPackageNameChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    minSdk: String,
    onMinSdkChange: (String) -> Unit,
    useKts: Boolean,
    onUseKtsChange: (Boolean) -> Unit,
    errorMessage: String?,
    onBack: () -> Unit,
    onCreate: () -> Unit
) {
    val languages = listOf("Kotlin", "Java", "Python", "HTML", "C")
    var languageExpanded by remember { mutableStateOf(false) }

    val sdkList = listOf(
        "API 21: Android 5.0 (Lollipop)",
        "API 24: Android 7.0 (Nougat)",
        "API 26: Android 8.0 (Oreo)",
        "API 30: Android 11 (Red Velvet Cake)",
        "API 33: Android 13 (Tiramisu)",
        "API 34: Android 14 (Upside Down Cake)",
        "API 36: Android 16 (Baklava)"
    )
    var sdkExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = IdeDarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Surface(
                color = IdeDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = IdeTextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back")
                    }

                    Button(
                        onClick = onCreate,
                        enabled = projectName.isNotBlank() && errorMessage == null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IdeAccentPeach,
                            contentColor = Color(0xFF28180E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IdeHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // Rounded container matching Screenshot 1
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(IdeDarkSurface)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Project Configuration",
                    color = IdeTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Project name
                OutlinedTextField(
                    value = projectName,
                    onValueChange = onProjectNameChange,
                    label = { Text("Project name", color = if (errorMessage != null) Color(0xFFF87171) else IdeTextSecondary) },
                    trailingIcon = {
                        if (errorMessage != null) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color(0xFFF87171)
                            )
                        }
                    },
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFF87171),
                                fontSize = 12.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (errorMessage != null) Color(0xFFF87171) else IdeAccentPeach,
                        unfocusedBorderColor = if (errorMessage != null) Color(0xFFF87171) else IdeDarkOutline,
                        focusedTextColor = IdeTextPrimary,
                        unfocusedTextColor = IdeTextPrimary,
                        focusedContainerColor = IdeDarkCard,
                        unfocusedContainerColor = IdeDarkCard
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Package name
                OutlinedTextField(
                    value = packageName,
                    onValueChange = onPackageNameChange,
                    label = { Text("Package name", color = IdeTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IdeAccentPeach,
                        unfocusedBorderColor = IdeDarkOutline,
                        focusedTextColor = IdeTextPrimary,
                        unfocusedTextColor = IdeTextPrimary,
                        focusedContainerColor = IdeDarkCard,
                        unfocusedContainerColor = IdeDarkCard
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Project location
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text("Project location", color = IdeTextSecondary) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "Select Location",
                            tint = IdeTextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IdeAccentPeach,
                        unfocusedBorderColor = IdeDarkOutline,
                        focusedTextColor = IdeTextPrimary,
                        unfocusedTextColor = IdeTextPrimary,
                        focusedContainerColor = IdeDarkCard,
                        unfocusedContainerColor = IdeDarkCard
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Language dropdown
                ExposedDropdownMenuBox(
                    expanded = languageExpanded,
                    onExpandedChange = { languageExpanded = !languageExpanded }
                ) {
                    OutlinedTextField(
                        value = language,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Language", color = IdeTextSecondary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IdeAccentPeach,
                            unfocusedBorderColor = IdeDarkOutline,
                            focusedTextColor = IdeTextPrimary,
                            unfocusedTextColor = IdeTextPrimary,
                            focusedContainerColor = IdeDarkCard,
                            unfocusedContainerColor = IdeDarkCard
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = languageExpanded,
                        onDismissRequest = { languageExpanded = false },
                        modifier = Modifier.background(IdeDarkSurfaceVariant)
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang, color = IdeTextPrimary) },
                                onClick = {
                                    onLanguageChange(lang)
                                    languageExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Minimum SDK dropdown
                ExposedDropdownMenuBox(
                    expanded = sdkExpanded,
                    onExpandedChange = { sdkExpanded = !sdkExpanded }
                ) {
                    OutlinedTextField(
                        value = minSdk,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Minimum SDK", color = IdeTextSecondary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sdkExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IdeAccentPeach,
                            unfocusedBorderColor = IdeDarkOutline,
                            focusedTextColor = IdeTextPrimary,
                            unfocusedTextColor = IdeTextPrimary,
                            focusedContainerColor = IdeDarkCard,
                            unfocusedContainerColor = IdeDarkCard
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = sdkExpanded,
                        onDismissRequest = { sdkExpanded = false },
                        modifier = Modifier.background(IdeDarkSurfaceVariant)
                    ) {
                        sdkList.forEach { sdk ->
                            DropdownMenuItem(
                                text = { Text(sdk, color = IdeTextPrimary) },
                                onClick = {
                                    onMinSdkChange(sdk)
                                    sdkExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Use Gradle Kotlin DSL (.kts) switch toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Use Gradle Kotlin DSL (.kts)",
                        color = IdeTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Switch(
                        checked = useKts,
                        onCheckedChange = onUseKtsChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = IdeAccentPeach,
                            checkedTrackColor = Color(0xFF3F3730),
                            uncheckedThumbColor = IdeTextSecondary,
                            uncheckedTrackColor = Color(0xFF282631)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
