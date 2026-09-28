package com.example.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.components.IdeHeader
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
    projects: List<ProjectEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onCreateProject: () -> Unit,
    onOpenProject: (ProjectEntity) -> Unit,
    onCloneGit: () -> Unit,
    onOpenTerminal: () -> Unit,
    onPreferences: () -> Unit,
    onIdeConfig: () -> Unit,
    onDocumentation: () -> Unit,
    onCloudSync: () -> Unit,
    onSupportDeveloper: () -> Unit,
    onOpenTelegram: () -> Unit = {},
    onOpenSvgIcons: () -> Unit = {},
    onOpenAiChat: () -> Unit = {},
    onDeleteProject: (Long) -> Unit = {}
) {
    // Clean mode state: 0 = "Get Started" (Screenshot 3), 1 = "Open Project" (Screenshot 4)
    var currentViewMode by remember { mutableStateOf(0) }

    BackHandler(enabled = currentViewMode != 0) {
        currentViewMode = 0
    }

    Scaffold(
        containerColor = IdeDarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentViewMode == 0) {
                // SCREENSHOT 3: Android Studio Welcome / Get Started Screen
                IdeHeader()

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Get started",
                    color = IdeTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Start your new awesome project!",
                    color = IdeTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable actions list with zero duplicate elements
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Telegram Promotional Banner
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenTelegram() },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF18222D)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF229ED9))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF229ED9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Join Official Telegram Channel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("@devcraftupdates • Compiler Updates & Templates", color = Color(0xFF38BDF8), fontSize = 11.sp)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF38BDF8))
                            }
                        }
                    }

                    // Primary Create Project Card
                    item {
                        MenuActionCard(
                            icon = Icons.Default.Add,
                            label = "Create project",
                            isPrimary = true,
                            onClick = onCreateProject
                        )
                    }

                    // Open existing project Card - switches view cleanly to Open Project view
                    item {
                        MenuActionCard(
                            icon = Icons.Default.Folder,
                            label = "Open existing project (${projects.size})",
                            onClick = { currentViewMode = 1 }
                        )
                    }

                    // AI Assistant Full Page Chat Card
                    item {
                        MenuActionCard(
                            icon = Icons.Default.AutoAwesome,
                            label = "DevCraft AI Assistant (Full Page Chat)",
                            customTint = Color(0xFF38BDF8),
                            onClick = onOpenAiChat
                        )
                    }

                    // Pre-loaded SVG Icons Pack Card
                    item {
                        MenuActionCard(
                            icon = Icons.Default.Category,
                            label = "SVG & Vector Icons Pack (100+ Icons)",
                            customTint = IdeAccentPeach,
                            onClick = onOpenSvgIcons
                        )
                    }

                    item {
                        MenuActionCard(
                            icon = Icons.Default.AltRoute,
                            label = "Clone git repository",
                            onClick = onCloneGit
                        )
                    }

                    item {
                        MenuActionCard(
                            icon = Icons.Default.Terminal,
                            label = "Terminal",
                            onClick = onOpenTerminal
                        )
                    }

                    item {
                        MenuActionCard(
                            icon = Icons.Default.Settings,
                            label = "Preferences",
                            onClick = onPreferences
                        )
                    }

                    item {
                        MenuActionCard(
                            icon = Icons.Default.Build,
                            label = "IDE Configurations",
                            onClick = onIdeConfig
                        )
                    }

                    item {
                        MenuActionCard(
                            icon = Icons.Default.MenuBook,
                            label = "Documentation",
                            onClick = onDocumentation
                        )
                    }

                    item {
                        MenuActionCard(
                            icon = Icons.Default.CloudSync,
                            label = "Cloud Sync & Backup",
                            onClick = onCloudSync
                        )
                    }

                    item {
                        MenuActionCard(
                            icon = Icons.Default.Favorite,
                            label = "Support Developer (8791738300@fam)",
                            customTint = Color(0xFFF87171),
                            onClick = onSupportDeveloper
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                // SCREENSHOT 4: Dedicated Open Project Screen
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentViewMode = 0 }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Welcome",
                                tint = IdeTextPrimary
                            )
                        }
                        Text(
                            text = "Open Project",
                            color = IdeTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }

                    // "+ import project" pill badge matching Screenshot 4
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(IdeAccentPeach.copy(alpha = 0.2f))
                            .border(1.dp, IdeAccentPeach.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .clickable { onCreateProject() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = IdeAccentPeach,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "import project",
                                color = IdeAccentPeach,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search field matching Screenshot 4
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search projects...", color = IdeTextTertiary, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IdeTextTertiary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = IdeTextTertiary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = IdeDarkSurfaceVariant,
                        unfocusedContainerColor = IdeDarkSurfaceVariant,
                        focusedBorderColor = IdeAccentPeach,
                        unfocusedBorderColor = IdeDarkOutline,
                        focusedTextColor = IdeTextPrimary,
                        unfocusedTextColor = IdeTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (projects.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = IdeTextTertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "No projects matching '$searchQuery'" else "No projects yet",
                                color = IdeTextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onCreateProject,
                                colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach)
                            ) {
                                Text("Create First Project", color = Color(0xFF28180E))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .clickable { onSupportDeveloper() },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D24)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Support Developer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("UPI: 8791738300@fam", color = Color(0xFFF87171), fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(projects, key = { it.id }) { project ->
                            ProjectRowItem(
                                project = project,
                                onClick = { onOpenProject(project) },
                                onDelete = { onDeleteProject(project.id) }
                            )
                        }
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSupportDeveloper() },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D24)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Support DevCraft Developer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("UPI ID: 8791738300@fam", color = Color(0xFFF87171), fontSize = 11.sp)
                                    }
                                    Text("Contribute ❤️", color = Color(0xFFF87171), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuActionCard(
    icon: ImageVector,
    label: String,
    isPrimary: Boolean = false,
    customTint: Color? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary) Color(0xFF26242D) else IdeDarkSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPrimary) IdeAccentPeach.copy(alpha = 0.5f)
            else if (customTint != null) customTint.copy(alpha = 0.5f)
            else IdeDarkOutline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = customTint ?: (if (isPrimary) IdeAccentPeach else IdeTextPrimary),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                color = customTint ?: IdeTextPrimary,
                fontSize = 15.sp,
                fontWeight = if (isPrimary || customTint != null) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ProjectRowItem(
    project: ProjectEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit = {}
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = IdeDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = project.name,
                        color = IdeTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF3F3730))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Recent",
                            color = IdeAccentPeach,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${project.location}/${project.name}",
                    color = IdeTextTertiary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ${project.language}",
                        color = IdeAccentGreen,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "• ${project.template}",
                        color = IdeTextSecondary,
                        fontSize = 11.sp
                    )
                    if (project.customLogoUri.isNotBlank()) {
                        Text(
                            text = "• Custom Logo",
                            color = IdeAccentPeach,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Project",
                    tint = IdeTextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Project", color = IdeTextPrimary) },
            text = { Text("Are you sure you want to delete '${project.name}'?", color = IdeTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF87171))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = IdeTextSecondary)
                }
            },
            containerColor = IdeDarkSurface
        )
    }
}
