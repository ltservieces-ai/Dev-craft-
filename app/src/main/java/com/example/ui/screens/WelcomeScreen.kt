package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
    onSupportDeveloper: () -> Unit
) {
    var showProjectsSheet by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(20.dp))

            // Scrollable actions or project list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Primary Create Project Card
                item {
                    MenuActionCard(
                        icon = Icons.Default.Add,
                        label = "Create project",
                        isPrimary = true,
                        onClick = onCreateProject
                    )
                }

                // Open existing project Card
                item {
                    MenuActionCard(
                        icon = Icons.Default.Folder,
                        label = "Open existing project (${projects.size})",
                        onClick = { showProjectsSheet = !showProjectsSheet }
                    )
                }

                // If user toggles or wants to see projects directly:
                if (showProjectsSheet) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = IdeDarkSurface),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Open Project",
                                        color = IdeTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(IdeAccentPeach.copy(alpha = 0.2f))
                                            .border(1.dp, IdeAccentPeach.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                            .clickable { onCreateProject() }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.FolderOpen,
                                                contentDescription = null,
                                                tint = IdeAccentPeach,
                                                modifier = Modifier.size(14.dp)
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

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = onSearchChange,
                                    placeholder = { Text("Search projects...", color = IdeTextTertiary, fontSize = 13.sp) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IdeTextTertiary) },
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

                                Spacer(modifier = Modifier.height(10.dp))

                                if (projects.isEmpty()) {
                                    Text(
                                        text = "No projects found.",
                                        color = IdeTextSecondary,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                } else {
                                    projects.forEach { project ->
                                        ProjectRowItem(
                                            project = project,
                                            onClick = { onOpenProject(project) }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = IdeDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.name,
                    color = IdeTextPrimary,
                    fontSize = 14.sp,
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
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "${project.location}/${project.name}",
                color = IdeTextTertiary,
                fontSize = 11.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
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
            }
        }
    }
}
