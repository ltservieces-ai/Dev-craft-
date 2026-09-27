package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.dialogs.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.IdeViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    private val viewModel: IdeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val projects by viewModel.projects.collectAsStateWithLifecycle()
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
            val activeProject by viewModel.activeProject.collectAsStateWithLifecycle()
            val projectFiles by viewModel.projectFiles.collectAsStateWithLifecycle()
            val currentFile by viewModel.currentFile.collectAsStateWithLifecycle()
            val openTabs by viewModel.openTabs.collectAsStateWithLifecycle()
            val editorCode by viewModel.editorCode.collectAsStateWithLifecycle()
            val editorFontSize by viewModel.editorFontSize.collectAsStateWithLifecycle()

            val isBuilding by viewModel.isBuilding.collectAsStateWithLifecycle()
            val buildLogs by viewModel.buildLogs.collectAsStateWithLifecycle()
            val appLogs by viewModel.appLogs.collectAsStateWithLifecycle()
            val ideLogs by viewModel.ideLogs.collectAsStateWithLifecycle()
            val diagnostics by viewModel.diagnostics.collectAsStateWithLifecycle()

            val hasTerminalSession by viewModel.hasTerminalSession.collectAsStateWithLifecycle()
            val terminalLogs by viewModel.terminalLogs.collectAsStateWithLifecycle()
            val terminalInput by viewModel.terminalInput.collectAsStateWithLifecycle()
            val isBottomSheetExpanded by viewModel.isBottomSheetExpanded.collectAsStateWithLifecycle()
            val activeBottomTab by viewModel.activeBottomTab.collectAsStateWithLifecycle()

            // Dialog states
            val showPermissionsDialog by viewModel.showPermissionsDialog.collectAsStateWithLifecycle()
            val showPushUpdatesDialog by viewModel.showPushUpdatesDialog.collectAsStateWithLifecycle()
            val showAppLogoDialog by viewModel.showAppLogoDialog.collectAsStateWithLifecycle()
            val showGitHubDialog by viewModel.showGitHubDialog.collectAsStateWithLifecycle()
            val showCloudSyncDialog by viewModel.showCloudSyncDialog.collectAsStateWithLifecycle()
            val showPreferencesDialog by viewModel.showPreferencesDialog.collectAsStateWithLifecycle()

            val configName by viewModel.configName.collectAsStateWithLifecycle()
            val configPackage by viewModel.configPackage.collectAsStateWithLifecycle()
            val configLocation by viewModel.configLocation.collectAsStateWithLifecycle()
            val configLanguage by viewModel.configLanguage.collectAsStateWithLifecycle()
            val configMinSdk by viewModel.configMinSdk.collectAsStateWithLifecycle()
            val configUseKts by viewModel.configUseKts.collectAsStateWithLifecycle()
            val configNameError by viewModel.configNameError.collectAsStateWithLifecycle()

            val githubUsername by viewModel.githubUsername.collectAsStateWithLifecycle()
            val githubToken by viewModel.githubToken.collectAsStateWithLifecycle()
            val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsStateWithLifecycle()
            val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

            val snackbarHostState = remember { SnackbarHostState() }

            // Runtime Notification Permission launcher
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(context, "Notifications permission needed for build alerts", Toast.LENGTH_SHORT).show()
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            LaunchedEffect(userMessage) {
                userMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearUserMessage()
                }
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        Screen.Splash -> {
                            SplashScreen(
                                onDismiss = { viewModel.dismissSplash() }
                            )
                        }
                        Screen.Welcome -> {
                            WelcomeScreen(
                                projects = projects,
                                searchQuery = searchQuery,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onCreateProject = { viewModel.navigateTo(Screen.ChooseTemplate) },
                                onOpenProject = { viewModel.openProject(it) },
                                onCloneGit = { viewModel.setShowGitHubDialog(true) },
                                onOpenTerminal = {
                                    if (projects.isNotEmpty()) {
                                        viewModel.openProject(projects.first())
                                        viewModel.setBottomSheetExpanded(true)
                                        viewModel.setActiveBottomTab(com.example.viewmodel.BottomTab.TERMINAL)
                                    } else {
                                        viewModel.navigateTo(Screen.ChooseTemplate)
                                    }
                                },
                                onPreferences = { viewModel.setShowPreferencesDialog(true) },
                                onIdeConfig = { viewModel.setShowPreferencesDialog(true) },
                                onDocumentation = {
                                    viewModel.openProject(projects.firstOrNull() ?: return@WelcomeScreen)
                                    viewModel.selectTab("settings.gradle")
                                },
                                onCloudSync = { viewModel.setShowCloudSyncDialog(true) },
                                onSupportDeveloper = { viewModel.navigateTo(Screen.SupportDeveloper) }
                            )
                        }
                        Screen.SupportDeveloper -> {
                            SupportDeveloperScreen(
                                onBack = { viewModel.navigateTo(Screen.Welcome) }
                            )
                        }
                        Screen.ChooseTemplate -> {
                            ChooseTemplateScreen(
                                onTemplateSelected = { template ->
                                    viewModel.selectTemplate(template)
                                },
                                onBack = { viewModel.navigateTo(Screen.Welcome) }
                            )
                        }
                        Screen.ProjectConfig -> {
                            ProjectConfigScreen(
                                projectName = configName,
                                onProjectNameChange = { viewModel.setConfigName(it) },
                                packageName = configPackage,
                                onPackageNameChange = { viewModel.setConfigPackage(it) },
                                location = configLocation,
                                onLocationChange = { viewModel.setConfigLocation(it) },
                                language = configLanguage,
                                onLanguageChange = { viewModel.setConfigLanguage(it) },
                                minSdk = configMinSdk,
                                onMinSdkChange = { viewModel.setConfigMinSdk(it) },
                                useKts = configUseKts,
                                onUseKtsChange = { viewModel.setConfigUseKts(it) },
                                errorMessage = configNameError,
                                onBack = { viewModel.navigateTo(Screen.ChooseTemplate) },
                                onCreate = { viewModel.confirmCreateProject() }
                            )
                        }
                        Screen.Workspace -> {
                            activeProject?.let { proj ->
                                WorkspaceScreen(
                                    context = context,
                                    project = proj,
                                    files = projectFiles,
                                    currentFile = currentFile,
                                    openTabs = openTabs,
                                    editorCode = editorCode,
                                    fontSize = editorFontSize,
                                    isBuilding = isBuilding,
                                    buildLogs = buildLogs,
                                    appLogs = appLogs,
                                    ideLogs = ideLogs,
                                    diagnostics = diagnostics,
                                    hasTerminalSession = hasTerminalSession,
                                    terminalLogs = terminalLogs,
                                    terminalInput = terminalInput,
                                    isBottomSheetExpanded = isBottomSheetExpanded,
                                    activeBottomTab = activeBottomTab,
                                    onTabSelected = { viewModel.setActiveBottomTab(it) },
                                    onToggleBottomSheet = { viewModel.toggleBottomSheet() },
                                    onSetBottomSheetExpanded = { viewModel.setBottomSheetExpanded(it) },
                                    onFileSelected = { viewModel.openFile(it) },
                                    onSelectOpenTab = { viewModel.selectTab(it) },
                                    onCloseOpenTab = { viewModel.closeTab(it) },
                                    onCodeChange = { viewModel.updateEditorCode(it) },
                                    onAddNewFile = { viewModel.createNewFileInProject(it) },
                                    onDeleteFile = { viewModel.deleteFileFromProject(it) },
                                    onRunBuild = { viewModel.runBuildAndInstall(context) },
                                    onInstallApk = { viewModel.promptInstallApk(context) },
                                    onShareApk = { viewModel.shareApk(context) },
                                    onExportCrossPlatform = { viewModel.exportCrossPlatformBundle(context) },
                                    onClearBuildLogs = { viewModel.clearBuildLogs() },
                                    onTerminalInputChange = { viewModel.setTerminalInput(it) },
                                    onTerminalKeyClick = { viewModel.appendTerminalKey(it) },
                                    onExecuteTerminalCommand = { viewModel.executeTerminalCommand() },
                                    onInitTerminal = { viewModel.initializeTerminal() },
                                    onOpenPermissions = { viewModel.setShowPermissionsDialog(true) },
                                    onOpenAppLogo = { viewModel.setShowAppLogoDialog(true) },
                                    onOpenPushUpdates = { viewModel.setShowPushUpdatesDialog(true) },
                                    onOpenGitHub = { viewModel.setShowGitHubDialog(true) },
                                    onOpenCloudSync = { viewModel.setShowCloudSyncDialog(true) },
                                    onOpenPreferences = { viewModel.setShowPreferencesDialog(true) },
                                    onCloseProject = { viewModel.navigateTo(Screen.Welcome) }
                                )
                            }
                        }
                    }

                    // Dialog Overlays
                    if (showPermissionsDialog && activeProject != null) {
                        PermissionsDashboardDialog(
                            project = activeProject!!,
                            onTogglePermission = { type, enabled ->
                                viewModel.togglePermission(type, enabled)
                            },
                            onDismiss = { viewModel.setShowPermissionsDialog(false) }
                        )
                    }

                    if (showAppLogoDialog && activeProject != null) {
                        AppLogoDialog(
                            project = activeProject!!,
                            onSaveLogo = { color, symbol ->
                                viewModel.updateAppLogo(color, symbol)
                            },
                            onDismiss = { viewModel.setShowAppLogoDialog(false) }
                        )
                    }

                    if (showPushUpdatesDialog && activeProject != null) {
                        PushUpdatesDialog(
                            project = activeProject!!,
                            onPush = { code, name, changelog ->
                                viewModel.pushUpdate(code, name, changelog)
                            },
                            onDismiss = { viewModel.setShowPushUpdatesDialog(false) }
                        )
                    }

                    if (showGitHubDialog && activeProject != null) {
                        GitHubDialog(
                            project = activeProject!!,
                            currentUsername = githubUsername,
                            currentToken = githubToken,
                            onSaveCredentials = { token, user ->
                                viewModel.setGitHubCredentials(token, user)
                            },
                            onCreateRepo = { name, isPriv ->
                                viewModel.createGitHubRepository(name, isPriv)
                            },
                            onDismiss = { viewModel.setShowGitHubDialog(false) }
                        )
                    }

                    if (showCloudSyncDialog) {
                        CloudSyncDialog(
                            status = cloudSyncStatus,
                            onSyncNow = { viewModel.syncCloudBackup() },
                            onDismiss = { viewModel.setShowCloudSyncDialog(false) }
                        )
                    }

                    if (showPreferencesDialog) {
                        PreferencesDialog(
                            isDarkTheme = isDarkTheme,
                            onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                            fontSize = editorFontSize,
                            onFontSizeChange = { viewModel.setFontSize(it) },
                            onDismiss = { viewModel.setShowPreferencesDialog(false) }
                        )
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}
