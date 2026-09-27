package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectFileEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.BottomTab
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    context: Context,
    project: ProjectEntity,
    files: List<ProjectFileEntity>,
    currentFile: ProjectFileEntity?,
    openTabs: List<String>,
    editorCode: String,
    fontSize: Int,
    isBuilding: Boolean,
    buildLogs: List<String>,
    appLogs: List<String>,
    ideLogs: List<String>,
    diagnostics: List<String>,
    hasTerminalSession: Boolean,
    terminalLogs: String,
    terminalInput: String,
    isBottomSheetExpanded: Boolean,
    activeBottomTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    onToggleBottomSheet: () -> Unit,
    onSetBottomSheetExpanded: (Boolean) -> Unit,
    onFileSelected: (ProjectFileEntity) -> Unit,
    onSelectOpenTab: (String) -> Unit,
    onCloseOpenTab: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onAddNewFile: (String) -> Unit,
    onDeleteFile: (String) -> Unit,
    onRunBuild: () -> Unit,
    onInstallApk: () -> Unit,
    onShareApk: () -> Unit,
    onExportCrossPlatform: () -> Unit,
    onClearBuildLogs: () -> Unit,
    onTerminalInputChange: (String) -> Unit,
    onTerminalKeyClick: (String) -> Unit,
    onExecuteTerminalCommand: () -> Unit,
    onInitTerminal: () -> Unit,
    onOpenPermissions: () -> Unit,
    onOpenAppLogo: () -> Unit,
    onOpenPushUpdates: () -> Unit,
    onOpenGitHub: () -> Unit,
    onOpenCloudSync: () -> Unit,
    onOpenPreferences: () -> Unit,
    onCloseProject: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showNewFileDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }

    BackHandler {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (isBottomSheetExpanded) {
            onSetBottomSheetExpanded(false)
        } else {
            onCloseProject()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = IdeDarkSurface,
                modifier = Modifier.width(300.dp)
            ) {
                FileTreeDrawer(
                    project = project,
                    files = files,
                    currentFile = currentFile,
                    onFileClick = { file ->
                        onFileSelected(file)
                        scope.launch { drawerState.close() }
                    },
                    onBackToProjects = onCloseProject,
                    onAddNewFile = { showNewFileDialog = true },
                    onDeleteFile = onDeleteFile,
                    onOpenSettings = onOpenPermissions
                )
            }
        }
    ) {
        Scaffold(
            containerColor = IdeDarkBackground,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                // Top App Bar matching Screenshots 8 & 9
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(IdeDarkBackground)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Drawer",
                            tint = IdeTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Run / Stop button
                    IconButton(
                        onClick = onRunBuild,
                        modifier = Modifier.size(40.dp)
                    ) {
                        if (isBuilding) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(2.dp, Color(0xFFF87171), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(0xFFF87171))
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Run App",
                                tint = IdeAccentGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Build / Device Icon
                    IconButton(
                        onClick = {
                            onTabSelected(BottomTab.BUILD_OUTPUT)
                            onSetBottomSheetExpanded(true)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = "Device",
                            tint = IdeTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Permissions Dashboard quick icon
                    IconButton(onClick = onOpenPermissions) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Permissions Dashboard",
                            tint = IdeAccentPeach,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Overflow Menu
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = IdeTextSecondary
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                            modifier = Modifier.background(IdeDarkSurfaceVariant)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Direct Install APK", color = IdeAccentGreen) },
                                onClick = {
                                    showOverflowMenu = false
                                    onInstallApk()
                                },
                                leadingIcon = { Icon(Icons.Default.Download, contentDescription = null, tint = IdeAccentGreen) }
                            )
                            DropdownMenuItem(
                                text = { Text("Share APK", color = IdeTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    onShareApk()
                                },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = IdeAccentPeach) }
                            )
                            DropdownMenuItem(
                                text = { Text("Export iOS & Android Bundle", color = IdeTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    onExportCrossPlatform()
                                },
                                leadingIcon = { Icon(Icons.Default.Archive, contentDescription = null, tint = IdeAccentPeach) }
                            )
                            HorizontalDivider(color = IdeDarkOutline)
                            DropdownMenuItem(
                                text = { Text("App Permissions Dashboard", color = IdeTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    onOpenPermissions()
                                },
                                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = IdeTextSecondary) }
                            )
                            DropdownMenuItem(
                                text = { Text("Push App Update (v${project.versionName})", color = IdeTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    onOpenPushUpdates()
                                },
                                leadingIcon = { Icon(Icons.Default.CloudUpload, contentDescription = null, tint = IdeTextSecondary) }
                            )
                            DropdownMenuItem(
                                text = { Text("App Logo & Icon", color = IdeTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    onOpenAppLogo()
                                },
                                leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null, tint = IdeTextSecondary) }
                            )
                            DropdownMenuItem(
                                text = { Text("GitHub Repository", color = IdeTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    onOpenGitHub()
                                },
                                leadingIcon = { Icon(Icons.Default.AltRoute, contentDescription = null, tint = IdeTextSecondary) }
                            )
                            DropdownMenuItem(
                                text = { Text("Cloud Sync & Backup", color = IdeTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    onOpenCloudSync()
                                },
                                leadingIcon = { Icon(Icons.Default.CloudSync, contentDescription = null, tint = IdeTextSecondary) }
                            )
                            DropdownMenuItem(
                                text = { Text("Preferences", color = IdeTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    onOpenPreferences()
                                },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = IdeTextSecondary) }
                            )
                            HorizontalDivider(color = IdeDarkOutline)
                            DropdownMenuItem(
                                text = { Text("Close Project", color = Color(0xFFF87171)) },
                                onClick = {
                                    showOverflowMenu = false
                                    onCloseProject()
                                },
                                leadingIcon = { Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFF87171)) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Main Center Area
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(if (isBottomSheetExpanded) 0.55f else 1f)
                    ) {
                        if (currentFile != null) {
                            CodeEditorView(
                                currentFile = currentFile,
                                code = editorCode,
                                openTabs = openTabs,
                                onTabSelected = onSelectOpenTab,
                                onTabClosed = onCloseOpenTab,
                                onCodeChange = onCodeChange,
                                fontSize = fontSize
                            )
                        } else {
                            // Empty State matching Screenshot 9 exactly!
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Android Studio",
                                    color = IdeTextPrimary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Open the left drawer for ", color = IdeTextSecondary, fontSize = 14.sp)
                                    Text(
                                        text = "files.",
                                        color = IdeTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.clickable { scope.launch { drawerState.open() } }
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Swipe up for ", color = IdeTextSecondary, fontSize = 14.sp)
                                    Text(
                                        text = "build output.",
                                        color = IdeTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.clickable {
                                            onTabSelected(BottomTab.BUILD_OUTPUT)
                                            onSetBottomSheetExpanded(true)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // If bottom sheet is expanded, render full BottomSheetPanel
                    if (isBottomSheetExpanded) {
                        BottomSheetPanel(
                            activeTab = activeBottomTab,
                            onTabSelected = onTabSelected,
                            buildLogs = buildLogs,
                            isBuilding = isBuilding,
                            onInstallApk = onInstallApk,
                            onShareApk = onShareApk,
                            onClearBuildLogs = onClearBuildLogs,
                            hasTerminalSession = hasTerminalSession,
                            terminalLogs = terminalLogs,
                            terminalInput = terminalInput,
                            onTerminalInputChange = onTerminalInputChange,
                            onTerminalKeyClick = onTerminalKeyClick,
                            onExecuteCommand = onExecuteTerminalCommand,
                            onInitTerminal = onInitTerminal,
                            appLogs = appLogs,
                            ideLogs = ideLogs,
                            diagnostics = diagnostics,
                            onCloseSheet = { onSetBottomSheetExpanded(false) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.45f)
                        )
                    }
                }

                // Bottom pill matching Screenshot 9: "Preparing - Swipe up or click for build output, logs and more."
                if (!isBottomSheetExpanded) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1E1D24).copy(alpha = 0.95f))
                            .border(1.dp, IdeDarkOutline, RoundedCornerShape(24.dp))
                            .clickable {
                                onTabSelected(BottomTab.BUILD_OUTPUT)
                                onSetBottomSheetExpanded(true)
                            }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isBuilding) "Building ${project.name}..." else "Preparing",
                                color = IdeTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Swipe up or click for build output, logs and more.",
                                color = IdeTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // New File Dialog
    if (showNewFileDialog) {
        AlertDialog(
            onDismissRequest = { showNewFileDialog = false },
            containerColor = IdeDarkSurface,
            title = { Text("Create New File", color = IdeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
            text = {
                Column {
                    Text("Enter relative path (e.g. app/src/main/kotlin/Utils.kt or script.py):", color = IdeTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
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
                        if (newFileName.isNotBlank()) {
                            onAddNewFile(newFileName)
                            newFileName = ""
                            showNewFileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach)
                ) {
                    Text("Create", color = Color(0xFF28180E))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFileDialog = false }) {
                    Text("Cancel", color = IdeTextSecondary)
                }
            }
        )
    }
}
