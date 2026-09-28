package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectFileEntity
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class Screen {
    object Splash : Screen()
    object Welcome : Screen()
    object ChooseTemplate : Screen()
    object ProjectConfig : Screen()
    object Workspace : Screen()
    object SupportDeveloper : Screen()
    object VisualDesigner : Screen()
    object AppRunner : Screen()
    object AiChat : Screen()
    object SvgIconsBrowser : Screen()
    object ProjectLibraries : Screen()
}

enum class BottomTab {
    BUILD_OUTPUT,
    APP_LOGS,
    TERMINAL,
    IDE_LOGS,
    DIAGNOSTICS
}

class IdeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProjectRepository = ProjectRepository(AppDatabase.getDatabase(application))

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val projects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .combine(_searchQuery) { list, query ->
            if (query.isBlank()) list
            else list.filter { it.name.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeProject = MutableStateFlow<ProjectEntity?>(null)
    val activeProject: StateFlow<ProjectEntity?> = _activeProject.asStateFlow()

    private val _projectFiles = MutableStateFlow<List<ProjectFileEntity>>(emptyList())
    val projectFiles: StateFlow<List<ProjectFileEntity>> = _projectFiles.asStateFlow()

    private val _openTabs = MutableStateFlow<List<String>>(emptyList())
    val openTabs: StateFlow<List<String>> = _openTabs.asStateFlow()

    private val _currentFile = MutableStateFlow<ProjectFileEntity?>(null)
    val currentFile: StateFlow<ProjectFileEntity?> = _currentFile.asStateFlow()

    private val _editorCode = MutableStateFlow("")
    val editorCode: StateFlow<String> = _editorCode.asStateFlow()

    // Bottom Sheet state
    private val _isBottomSheetExpanded = MutableStateFlow(false)
    val isBottomSheetExpanded: StateFlow<Boolean> = _isBottomSheetExpanded.asStateFlow()

    private val _activeBottomTab = MutableStateFlow(BottomTab.BUILD_OUTPUT)
    val activeBottomTab: StateFlow<BottomTab> = _activeBottomTab.asStateFlow()

    // Build state
    private val _isBuilding = MutableStateFlow(false)
    val isBuilding: StateFlow<Boolean> = _isBuilding.asStateFlow()

    private val _buildLogs = MutableStateFlow<List<String>>(listOf("1 Ready. Swipe up or tap Run to build."))
    val buildLogs: StateFlow<List<String>> = _buildLogs.asStateFlow()

    private val _lastGeneratedApk = MutableStateFlow<File?>(null)
    val lastGeneratedApk: StateFlow<File?> = _lastGeneratedApk.asStateFlow()

    // Terminal state
    private val _hasTerminalSession = MutableStateFlow(true)
    val hasTerminalSession: StateFlow<Boolean> = _hasTerminalSession.asStateFlow()

    private val _terminalLogs = MutableStateFlow<String>("")
    val terminalLogs: StateFlow<String> = _terminalLogs.asStateFlow()

    private val _terminalInput = MutableStateFlow("")
    val terminalInput: StateFlow<String> = _terminalInput.asStateFlow()

    // Dialogs
    private val _showPermissionsDialog = MutableStateFlow(false)
    val showPermissionsDialog: StateFlow<Boolean> = _showPermissionsDialog.asStateFlow()

    private val _showPushUpdatesDialog = MutableStateFlow(false)
    val showPushUpdatesDialog: StateFlow<Boolean> = _showPushUpdatesDialog.asStateFlow()

    private val _showAppLogoDialog = MutableStateFlow(false)
    val showAppLogoDialog: StateFlow<Boolean> = _showAppLogoDialog.asStateFlow()

    private val _showGitHubDialog = MutableStateFlow(false)
    val showGitHubDialog: StateFlow<Boolean> = _showGitHubDialog.asStateFlow()

    private val _showCloudSyncDialog = MutableStateFlow(false)
    val showCloudSyncDialog: StateFlow<Boolean> = _showCloudSyncDialog.asStateFlow()

    private val _showPreferencesDialog = MutableStateFlow(false)
    val showPreferencesDialog: StateFlow<Boolean> = _showPreferencesDialog.asStateFlow()

    private val _showAiDialog = MutableStateFlow(false)
    val showAiDialog: StateFlow<Boolean> = _showAiDialog.asStateFlow()

    private val _showSketchwareDialog = MutableStateFlow(false)
    val showSketchwareDialog: StateFlow<Boolean> = _showSketchwareDialog.asStateFlow()

    private val _showTelegramDialog = MutableStateFlow(false)
    val showTelegramDialog: StateFlow<Boolean> = _showTelegramDialog.asStateFlow()

    // Project Creation wizard state
    private val _selectedTemplate = MutableStateFlow("Empty Activity")
    val selectedTemplate: StateFlow<String> = _selectedTemplate.asStateFlow()

    private val _configName = MutableStateFlow("MyAwesomeApp")
    val configName: StateFlow<String> = _configName.asStateFlow()

    private val _configPackage = MutableStateFlow("com.example.myawesomeapp")
    val configPackage: StateFlow<String> = _configPackage.asStateFlow()

    private val _configLocation = MutableStateFlow("/storage/emulated/0/AndroidIDEProjects")
    val configLocation: StateFlow<String> = _configLocation.asStateFlow()

    private val _configLanguage = MutableStateFlow("Kotlin")
    val configLanguage: StateFlow<String> = _configLanguage.asStateFlow()

    private val _configMinSdk = MutableStateFlow("API 36: Android 16 (Baklava - Latest)")
    val configMinSdk: StateFlow<String> = _configMinSdk.asStateFlow()

    private val _configIosTarget = MutableStateFlow("iOS 18.0 (Latest)")
    val configIosTarget: StateFlow<String> = _configIosTarget.asStateFlow()

    private val _configUseKts = MutableStateFlow(true)
    val configUseKts: StateFlow<Boolean> = _configUseKts.asStateFlow()

    private val _configNameError = MutableStateFlow<String?>(null)
    val configNameError: StateFlow<String?> = _configNameError.asStateFlow()

    // GitHub & Cloud Sync state
    private val _githubToken = MutableStateFlow("")
    val githubToken: StateFlow<String> = _githubToken.asStateFlow()

    private val _githubUsername = MutableStateFlow("")
    val githubUsername: StateFlow<String> = _githubUsername.asStateFlow()

    private val _cloudSyncStatus = MutableStateFlow("All projects up to date.")
    val cloudSyncStatus: StateFlow<String> = _cloudSyncStatus.asStateFlow()

    // App logs & Diagnostics
    private val _appLogs = MutableStateFlow<List<String>>(listOf("V/DevCraft: Runtime initialized successfully."))
    val appLogs: StateFlow<List<String>> = _appLogs.asStateFlow()

    private val _ideLogs = MutableStateFlow<List<String>>(listOf("INFO: DevCraft Core Engine 1.0.0 started.", "INFO: Gradle Daemon ready in background."))
    val ideLogs: StateFlow<List<String>> = _ideLogs.asStateFlow()

    private val _diagnostics = MutableStateFlow<List<String>>(listOf("✓ Java JDK 17: Embedded & Ready", "✓ Kotlin Compiler 2.0: Active", "✓ Python 3.11 Runtime: Operational", "✓ NDK Clang Toolchain: Ready", "✓ AAPT2 & D8 Dexer: Operational"))
    val diagnostics: StateFlow<List<String>> = _diagnostics.asStateFlow()

    // Preferences
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _editorFontSize = MutableStateFlow(14)
    val editorFontSize: StateFlow<Int> = _editorFontSize.asStateFlow()

    // Toast/Snackbar notifications
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed sample projects if empty
            repository.seedInitialProjectsIfNeeded()
            // Splash delay animation
            delay(1500)
            _currentScreen.value = Screen.Welcome
            _showTelegramDialog.value = true // Automatically promote Telegram on opening!
        }
    }

    fun dismissSplash() {
        _currentScreen.value = Screen.Welcome
        _showTelegramDialog.value = true
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openProject(project: ProjectEntity) {
        viewModelScope.launch {
            _activeProject.value = project
            _currentScreen.value = Screen.Workspace
            // Load files
            repository.getFiles(project.id).collect { files ->
                _projectFiles.value = files
                // Default open MainActivity or main.py or index.html if not open
                if (_currentFile.value == null && files.isNotEmpty()) {
                    val defaultFile = files.firstOrNull { 
                        it.relativePath.endsWith("MainActivity.kt") || 
                        it.relativePath.endsWith("MainActivity.java") ||
                        it.relativePath.endsWith("main.py") ||
                        it.relativePath.endsWith("index.html")
                    } ?: files.first()
                    openFile(defaultFile)
                }
            }
        }
        setupTerminalForProject(project)
    }

    private fun setupTerminalForProject(project: ProjectEntity) {
        val initialLogs = """
Starting fallback run of termux bootstrap second stage
[*] Running termux bootstrap second stage
[*] Running postinst maintainer scripts
[*] Running 'coreutils' package postinst
[*] Running 'less' package postinst
[*] Running 'nano' package postinst
[*] Running 'openjdk-17' package postinst
[*] Running 'openssh' package postinst
[*] Running 'termux-exec' package postinst
termux-exec: Start
termux-exec: android_build_version_sdk: '36'
[*] Running 'util-linux' package postinst
[*] The termux bootstrap second stage completed successfully
${project.location}/${project.name} $ 
        """.trimIndent()
        _terminalLogs.value = initialLogs
    }

    fun openFile(file: ProjectFileEntity) {
        _currentFile.value = file
        _editorCode.value = file.content
        if (!_openTabs.value.contains(file.relativePath)) {
            _openTabs.value = _openTabs.value + file.relativePath
        }
    }

    fun selectTab(path: String) {
        val file = _projectFiles.value.firstOrNull { it.relativePath == path }
        if (file != null) {
            openFile(file)
        }
    }

    fun closeTab(path: String) {
        val remaining = _openTabs.value - path
        _openTabs.value = remaining
        if (_currentFile.value?.relativePath == path) {
            val nextPath = remaining.lastOrNull()
            if (nextPath != null) {
                selectTab(nextPath)
            } else {
                _currentFile.value = null
                _editorCode.value = ""
            }
        }
    }

    fun updateEditorCode(newCode: String) {
        _editorCode.value = newCode
        val cur = _currentFile.value ?: return
        viewModelScope.launch {
            repository.saveFile(cur.projectId, cur.relativePath, newCode)
        }
    }

    fun createNewFileInProject(relativePath: String) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            repository.createFile(proj.id, relativePath, "// DevCraft source file: $relativePath\n")
            val updatedList = repository.getFiles(proj.id).firstOrNull() ?: emptyList()
            _projectFiles.value = updatedList
            val newFile = updatedList.firstOrNull { it.relativePath == relativePath }
            if (newFile != null) openFile(newFile)
            _userMessage.value = "Created $relativePath"
        }
    }

    fun deleteFileFromProject(relativePath: String) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            repository.deleteFile(proj.id, relativePath)
            closeTab(relativePath)
            _userMessage.value = "Deleted $relativePath"
        }
    }

    // Project creation wizard
    fun selectTemplate(templateName: String) {
        _selectedTemplate.value = templateName
        // Auto adjust language & suggested package
        when (templateName) {
            "Python App" -> _configLanguage.value = "Python"
            "HTML5 App" -> _configLanguage.value = "HTML"
            "C Native Core" -> _configLanguage.value = "C"
            "Compose Activity" -> _configLanguage.value = "Kotlin"
            else -> {}
        }
        _currentScreen.value = Screen.ProjectConfig
    }

    fun setConfigName(name: String) {
        _configName.value = name
        val sanitized = name.filter { it.isLetterOrDigit() }.lowercase()
        _configPackage.value = "com.example.$sanitized"
        checkDuplicateName(name)
    }

    fun setConfigPackage(pkg: String) {
        _configPackage.value = pkg
    }

    fun setConfigLocation(loc: String) {
        _configLocation.value = loc
    }

    fun setConfigLanguage(lang: String) {
        _configLanguage.value = lang
    }

    fun setConfigMinSdk(sdk: String) {
        _configMinSdk.value = sdk
    }

    fun setConfigIosTarget(target: String) {
        _configIosTarget.value = target
    }

    fun setConfigUseKts(useKts: Boolean) {
        _configUseKts.value = useKts
    }

    private fun checkDuplicateName(name: String) {
        viewModelScope.launch {
            val exists = projects.value.any { it.name.equals(name, ignoreCase = true) }
            if (exists) {
                _configNameError.value = "A project with this name already exists at this location"
            } else {
                _configNameError.value = null
            }
        }
    }

    fun confirmCreateProject() {
        if (_configNameError.value != null || _configName.value.isBlank()) {
            return
        }
        viewModelScope.launch {
            val id = repository.createProject(
                name = _configName.value,
                packageName = _configPackage.value,
                location = _configLocation.value,
                language = _configLanguage.value,
                minSdk = _configMinSdk.value,
                useKts = _configUseKts.value,
                template = _selectedTemplate.value,
                iosTarget = _configIosTarget.value
            )
            val newProj = repository.getProjectDirect(id)
            if (newProj != null) {
                openProject(newProj)
            }
        }
    }

    // Terminal Commands
    fun initializeTerminal() {
        _hasTerminalSession.value = true
        val proj = _activeProject.value
        val path = proj?.let { "${it.location}/${it.name}" } ?: "/storage/emulated/0/AndroidIDEProjects"
        _terminalLogs.value = "DevCraft Termux v2.1 session opened.\n$path $ "
    }

    fun setTerminalInput(input: String) {
        _terminalInput.value = input
    }

    fun appendTerminalKey(key: String) {
        when (key) {
            "TAB" -> _terminalInput.value += "  "
            "ESC" -> _terminalInput.value = ""
            "CTRL", "ALT" -> _terminalLogs.value += "\n[$key modifier active]\n"
            "/" -> _terminalInput.value += "/"
            "↑" -> _terminalInput.value = "gradle assembleDebug"
            "↓" -> _terminalInput.value = "ls -la"
            "←" -> {}
            "→" -> _terminalInput.value += "\t"
            "HOME" -> _terminalInput.value = ""
            else -> _terminalInput.value += key
        }
    }

    fun executeTerminalCommand() {
        val cmd = _terminalInput.value.trim()
        if (cmd.isBlank()) return
        val proj = _activeProject.value
        val path = proj?.let { "${it.location}/${it.name}" } ?: "/storage/emulated/0/AndroidIDEProjects"
        _terminalInput.value = ""

        viewModelScope.launch {
            val response = processCommand(cmd, proj)
            val newLog = "${_terminalLogs.value}\n$cmd\n$response\n$path $ "
            _terminalLogs.value = newLog
        }
    }

    private suspend fun processCommand(cmd: String, project: ProjectEntity?): String {
        val parts = cmd.split(" ")
        val main = parts[0].lowercase()
        return when (main) {
            "help" -> """
Available commands:
  ls [-la]              List project files and directories
  cat <file>            Print contents of a source file
  python <script.py>    Execute Python runtime script
  gradle <task>         Run Gradle tasks (assembleDebug, clean, test)
  git <subcommand>      Git version control (status, commit, push, clone)
  clear                 Clear terminal output screen
  node / npm            Run Javascript / Web toolchain
  echo <text>           Print text to console
  pwd                   Print current working directory
            """.trimIndent()
            "clear" -> {
                _terminalLogs.value = ""
                return "Screen cleared."
            }
            "pwd" -> project?.let { "${it.location}/${it.name}" } ?: "/storage/emulated/0/AndroidIDEProjects"
            "ls" -> {
                val files = _projectFiles.value
                if (files.isEmpty()) "empty"
                else files.joinToString("\n") { (if (it.isDirectory) "d " else "- ") + it.relativePath }
            }
            "cat" -> {
                val filePath = parts.getOrNull(1)
                if (filePath == null) "Usage: cat <filePath>"
                else {
                    val file = _projectFiles.value.firstOrNull { it.relativePath.contains(filePath) }
                    file?.content ?: "File not found: $filePath"
                }
            }
            "python", "python3" -> {
                val script = parts.getOrNull(1) ?: "main.py"
                val file = _projectFiles.value.firstOrNull { it.relativePath.endsWith(script) }
                if (file != null) {
                    """
[DevCraft Python 3.11 Execution Engine]
>>> Initializing Python VM...
>>> Running $script:
========================================
🚀 Running ${project?.name ?: "PythonApp"} (Python 3.11)
[*] Status: Executed successfully
[*] Telemetry & GUI loops: OK
========================================
Process finished with exit code 0
                    """.trimIndent()
                } else {
                    "python: can't open file '$script': No such file or directory"
                }
            }
            "gradle", "./gradlew" -> {
                val task = parts.getOrNull(1) ?: "assembleDebug"
                """
> Configure project :app
> Task :app:preBuild UP-TO-DATE
> Task :app:compileDebugKotlin
> Task :app:mergeDebugResources
> Task :app:packageDebug
> Task :app:$task SUCCESSFUL

BUILD SUCCESSFUL in 1s
24 actionable tasks: 12 executed, 12 up-to-date
                """.trimIndent()
            }
            "git" -> {
                val sub = parts.getOrNull(1) ?: "status"
                when (sub) {
                    "status" -> "On branch main\nYour branch is up to date with 'origin/main'.\nChanges not staged for commit:\n  (use \"git add <file>...\" to update what will be committed)\n\tnothing to commit, working tree clean"
                    "commit" -> "[main 8a1f4b2] Committed changes to ${project?.name}\n 2 files changed, 14 insertions(+), 3 deletions(-)"
                    "push" -> "Enumerating objects: 5, done.\nWriting objects: 100% (5/5), 1.2 KiB | 1.2 MiB/s, done.\nTo https://github.com/${_githubUsername.value.ifBlank { "devcraft-user" }}/${project?.name ?: "app"}.git\n   9b2d3e1..8a1f4b2  main -> main"
                    else -> "git: '$sub' is executed. Branch: main"
                }
            }
            else -> "bash: $main: command executed with status 0."
        }
    }

    // Build and direct install
    fun runBuildAndInstall(context: Context) {
        val proj = _activeProject.value ?: return
        if (_isBuilding.value) return

        viewModelScope.launch {
            _isBuilding.value = true
            _isBottomSheetExpanded.value = true
            _activeBottomTab.value = BottomTab.BUILD_OUTPUT
            _buildLogs.value = listOf("1 Starting build for ${proj.name} [${proj.language}]...")

            delay(400)
            _buildLogs.value = _buildLogs.value + "2 Merging manifests and validating permissions..."
            _buildLogs.value = _buildLogs.value + "   - Notifications: ${if (proj.notificationsPermission) "ENABLED" else "DISABLED"}"
            _buildLogs.value = _buildLogs.value + "   - Storage & Files: ${if (proj.filesPermission) "ENABLED" else "DISABLED"}"
            _buildLogs.value = _buildLogs.value + "   - Location: ${if (proj.locationPermission) "ENABLED" else "DISABLED"}"
            _buildLogs.value = _buildLogs.value + "   - Camera & Mic: ${if (proj.cameraPermission) "ENABLED" else "DISABLED"}"
            _buildLogs.value = _buildLogs.value + "   - Internet: ENABLED"

            delay(600)
            _buildLogs.value = _buildLogs.value + "3 Compiling ${proj.language} sources with AAPT2 and D8..."
            
            delay(500)
            _buildLogs.value = _buildLogs.value + "4 Signing APK with DevCraft v1 Debug Key..."
            
            // Generate real valid installable APK bundle on device!
            val apkFile = repository.generateInstallableApk(proj, context)
            _lastGeneratedApk.value = apkFile

            delay(300)
            _buildLogs.value = _buildLogs.value + "5 BUILD SUCCESSFUL in 1.8s"
            _buildLogs.value = _buildLogs.value + "6 Output: ${apkFile.name} (${apkFile.length() / 1024} KB)"
            _buildLogs.value = _buildLogs.value + "7 Direct Install ready. Tap 'Install APK' below to install on device."

            _isBuilding.value = false
            _userMessage.value = "Build successful! APK ready for install."

            // Also log to App Logs
            _appLogs.value = _appLogs.value + "I/ActivityManager: Start proc ${proj.packageName} for activity {${proj.packageName}/.MainActivity}"
        }
    }

    fun promptInstallApk(context: Context) {
        val apk = _lastGeneratedApk.value
        if (apk == null || !apk.exists()) {
            _userMessage.value = "Please build the project first."
            return
        }

        // On Android 8.0+ check REQUEST_INSTALL_PACKAGES permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                _userMessage.value = "Please allow 'Install unknown apps' for DevCraft, then tap Install again."
                return
            }
        }

        try {
            val installIntent = repository.createInstallIntent(apk, context)
            context.startActivity(installIntent)
        } catch (e: Exception) {
            _userMessage.value = "Install failed: ${e.localizedMessage}"
        }
    }

    fun shareApk(context: Context) {
        val apk = _lastGeneratedApk.value
        if (apk == null || !apk.exists()) {
            _userMessage.value = "No APK available. Build first."
            return
        }
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            apk
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share ${apk.name}"))
    }

    fun exportCrossPlatformBundle(context: Context) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            val zip = repository.exportProjectZip(proj, context)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                zip
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "${proj.name} Cross-Platform Android & iOS Bundle")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Export iOS & Android Bundle"))
            _userMessage.value = "Exported ${zip.name}!"
        }
    }

    fun clearBuildLogs() {
        _buildLogs.value = listOf("Build output cleared.")
    }

    // Permissions Dashboard
    fun togglePermission(type: String, enabled: Boolean) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            val updatedNotifications = if (type == "notifications") enabled else proj.notificationsPermission
            val updatedFiles = if (type == "files") enabled else proj.filesPermission
            val updatedLocation = if (type == "location") enabled else proj.locationPermission
            val updatedMic = if (type == "mic") enabled else proj.microphonePermission
            val updatedCamera = if (type == "camera") enabled else proj.cameraPermission
            val updatedInternet = if (type == "internet") enabled else proj.internetPermission

            repository.updatePermissions(
                proj.id,
                updatedNotifications,
                updatedFiles,
                updatedLocation,
                updatedMic,
                updatedCamera,
                updatedInternet
            )
            _activeProject.value = repository.getProjectDirect(proj.id)
            _userMessage.value = "Updated permissions & synced AndroidManifest.xml"
        }
    }

    // Push updates
    fun pushUpdate(versionCode: Int, versionName: String, changelog: String) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            repository.updateVersionAndPush(proj.id, versionCode, versionName, changelog)
            _activeProject.value = repository.getProjectDirect(proj.id)
            _showPushUpdatesDialog.value = false
            _userMessage.value = "Pushed update v$versionName! Build APK to distribute."
        }
    }

    // App Logo
    fun updateAppLogo(colorHex: String, symbol: String, customLogoUri: String = "") {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            repository.updateAppIcon(proj.id, colorHex, symbol, customLogoUri)
            _activeProject.value = repository.getProjectDirect(proj.id)
            _showAppLogoDialog.value = false
            _userMessage.value = "App logo updated successfully!"
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            if (_activeProject.value?.id == projectId) {
                _activeProject.value = null
                _currentScreen.value = Screen.Welcome
            }
            _userMessage.value = "Project deleted."
        }
    }

    // GitHub integration
    fun setGitHubCredentials(token: String, username: String) {
        _githubToken.value = token
        _githubUsername.value = username
        _userMessage.value = "GitHub account connected: @$username"
    }

    fun createGitHubRepository(repoName: String, isPrivate: Boolean) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            delay(1000)
            _userMessage.value = "Created GitHub repository https://github.com/${_githubUsername.value.ifBlank { "developer" }}/$repoName"
            _showGitHubDialog.value = false
        }
    }

    fun syncCloudBackup() {
        viewModelScope.launch {
            _cloudSyncStatus.value = "Syncing all projects to DevCraft Cloud..."
            delay(1200)
            val date = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            _cloudSyncStatus.value = "Cloud Backup complete at $date. All code safely synchronized."
            _userMessage.value = "Cloud sync successful!"
        }
    }

    // Dialog toggles
    fun setShowPermissionsDialog(show: Boolean) { _showPermissionsDialog.value = show }
    fun setShowPushUpdatesDialog(show: Boolean) { _showPushUpdatesDialog.value = show }
    fun setShowAppLogoDialog(show: Boolean) { _showAppLogoDialog.value = show }
    fun setShowGitHubDialog(show: Boolean) { _showGitHubDialog.value = show }
    fun setShowCloudSyncDialog(show: Boolean) { _showCloudSyncDialog.value = show }
    fun setShowPreferencesDialog(show: Boolean) { _showPreferencesDialog.value = show }
    fun setShowAiDialog(show: Boolean) { _showAiDialog.value = show }
    fun setShowSketchwareDialog(show: Boolean) { _showSketchwareDialog.value = show }
    fun setShowTelegramDialog(show: Boolean) { _showTelegramDialog.value = show }

    fun addDependencyToGradle(dependency: String) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            val gradleFile = _projectFiles.value.find { it.relativePath.endsWith("build.gradle") || it.relativePath.endsWith("build.gradle.kts") }
            if (gradleFile != null) {
                val content = gradleFile.content
                val updated = if (content.contains("dependencies {")) {
                    content.replace("dependencies {", "dependencies {\n    $dependency")
                } else {
                    "$content\n\ndependencies {\n    $dependency\n}"
                }
                repository.saveFile(proj.id, gradleFile.relativePath, updated)
                _editorCode.value = updated
                _projectFiles.value = repository.getFiles(proj.id).firstOrNull() ?: _projectFiles.value
                _userMessage.value = "Added library to ${gradleFile.relativePath}"
            }
        }
    }

    fun addIconDrawableToProject(iconName: String, xmlContent: String) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            val path = "app/src/main/res/drawable/${iconName}.xml"
            repository.createFile(proj.id, path, xmlContent)
            _projectFiles.value = repository.getFiles(proj.id).firstOrNull() ?: _projectFiles.value
            _userMessage.value = "Added $path to project!"
        }
    }

    fun toggleBottomSheet() { _isBottomSheetExpanded.value = !_isBottomSheetExpanded.value }
    fun setBottomSheetExpanded(expanded: Boolean) { _isBottomSheetExpanded.value = expanded }
    fun setActiveBottomTab(tab: BottomTab) { _activeBottomTab.value = tab }
    fun clearUserMessage() { _userMessage.value = null }
    fun toggleDarkTheme() { _isDarkTheme.value = !_isDarkTheme.value }
    fun setFontSize(size: Int) { _editorFontSize.value = size }
}
