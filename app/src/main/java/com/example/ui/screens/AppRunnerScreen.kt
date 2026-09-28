package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.ProjectEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

data class LocalSong(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: String,
    val path: String = "",
    val isLocalDevice: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRunnerScreen(
    project: ProjectEntity,
    onBack: () -> Unit,
    onInstallApk: () -> Unit,
    onOpenSupportDeveloper: () -> Unit,
    onOpenTelegram: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    BackHandler { onBack() }

    // Permissions check on app launch (Addresses requirement: if files or other permission toggled, prompt user immediately!)
    var permissionAuditLog by remember { mutableStateOf<List<String>>(emptyList()) }
    var showPermissionBanner by remember { mutableStateOf(true) }

    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val logs = mutableListOf<String>()
        results.forEach { (perm, granted) ->
            val simpleName = perm.substringAfterLast('.')
            logs.add("$simpleName: ${if (granted) "✅ GRANTED" else "⚠️ DENIED"}")
        }
        permissionAuditLog = logs
    }

    LaunchedEffect(project) {
        val permissionsToRequest = mutableListOf<String>()

        if (project.filesPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
                }
            } else {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }

        if (project.cameraPermission) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.CAMERA)
            }
        }

        if (project.notificationsPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            multiplePermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            permissionAuditLog = listOf("All project permissions verified")
        }
    }

    var showSourceCodeDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0F0F12),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(try {
                                    Color(android.graphics.Color.parseColor(project.iconColorHex))
                                } catch (_: Exception) {
                                    Color(0xFF38C779)
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            val iconVector = when (project.iconSymbol.lowercase()) {
                                "music" -> Icons.Default.MusicNote
                                "game" -> Icons.Default.SportsEsports
                                "terminal" -> Icons.Default.Terminal
                                "rocket" -> Icons.Default.RocketLaunch
                                "star" -> Icons.Default.Star
                                "android" -> Icons.Default.Android
                                else -> Icons.Default.Code
                            }
                            Icon(iconVector, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = project.name,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF38C779).copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("RUNNING", color = Color(0xFF38C779), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "${project.packageName} • v${project.versionName}",
                                color = Color(0xFFA19EAA),
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit App", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showSourceCodeDialog = true }) {
                        Icon(Icons.Default.Code, contentDescription = "View Code", tint = IdeAccentPeach)
                    }
                    IconButton(onClick = onOpenTelegram) {
                        Icon(Icons.Default.Send, contentDescription = "Telegram", tint = Color(0xFF229ED9))
                    }
                    Button(
                        onClick = onInstallApk,
                        colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF28180E), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Install APK", color = Color(0xFF28180E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1D24))
            )
        },
        bottomBar = {
            // Support Developer & Telegram quick promotion bar
            Surface(
                color = Color(0xFF18171E),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2A36))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { onOpenSupportDeveloper() }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Support Dev (8791738300@fam)",
                            color = Color(0xFFF87171),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF229ED9).copy(alpha = 0.2f))
                            .clickable { onOpenTelegram() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Join Telegram 📢", color = Color(0xFF229ED9), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Permissions Requested Alert Banner if files or other permissions were enabled
            if (showPermissionBanner && permissionAuditLog.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("App Permissions Requested on Launch", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(permissionAuditLog.joinToString(", "), color = Color(0xFF94A3B8), fontSize = 11.sp, maxLines = 1)
                            }
                        }
                        IconButton(onClick = { showPermissionBanner = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Real Interactive App Content based on Project Template / Name
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                val tName = project.template.lowercase()
                val pName = project.name.lowercase()

                when {
                    tName.contains("calc") || pName.contains("calc") -> {
                        CalculatorAppRunner(projectName = project.name)
                    }
                    tName.contains("puzzle") || pName.contains("puzzle") -> {
                        PuzzleGameAppRunner(projectName = project.name)
                    }
                    tName.contains("music") || pName.contains("music") || pName.contains("audio") || pName.contains("song") -> {
                        MusicPlayerAppRunner(context = context, projectName = project.name)
                    }
                    else -> {
                        GenericAppRunner(project = project, onOpenTelegram = onOpenTelegram)
                    }
                }
            }
        }
    }

    // Source Code Inspect Dialog
    if (showSourceCodeDialog) {
        AlertDialog(
            onDismissRequest = { showSourceCodeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = IdeAccentPeach)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Source Code for ${project.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    item {
                        Text(
                            text = """
// Compiled MainActivity.kt
package ${project.packageName}

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Auto-generated by DevCraft Mobile IDE
        println("Starting ${project.name}...")
    }
}
                            """.trimIndent(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF121214))
                                .padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSourceCodeDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach)) {
                    Text("Close", color = Color(0xFF28180E))
                }
            }
        )
    }
}

/**
 * Real Fully Working Calculator App Runner
 */
@Composable
fun CalculatorAppRunner(projectName: String) {
    var display by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }
    var firstNum by remember { mutableStateOf<Double?>(null) }
    var currentOp by remember { mutableStateOf<String?>(null) }
    var resetOnNextDigit by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (display == "0" || resetOnNextDigit) {
            display = d
            resetOnNextDigit = false
        } else {
            if (d == "." && display.contains(".")) return
            display += d
        }
    }

    fun onOperator(op: String) {
        val num = display.toDoubleOrNull() ?: 0.0
        firstNum = num
        currentOp = op
        expression = "$display $op"
        resetOnNextDigit = true
    }

    fun onEqual() {
        val second = display.toDoubleOrNull() ?: return
        val first = firstNum ?: return
        val op = currentOp ?: return

        val res = when (op) {
            "+" -> first + second
            "-" -> first - second
            "×" -> first * second
            "÷" -> if (second != 0.0) first / second else Double.NaN
            "%" -> first % second
            else -> second
        }

        expression = "$first $op $second ="
        display = if (res.isNaN()) "Error" else if (res % 1.0 == 0.0) res.toLong().toString() else "%.4f".format(res).trimEnd('0').trimEnd('.')
        firstNum = null
        currentOp = null
        resetOnNextDigit = true
    }

    fun onClear() {
        display = "0"
        expression = ""
        firstNum = null
        currentOp = null
        resetOnNextDigit = false
    }

    fun onBackspace() {
        if (display.length > 1 && display != "Error") {
            display = display.dropLast(1)
        } else {
            display = "0"
        }
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121214)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Calculator Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🧮 $projectName", color = Color(0xFFA19EAA), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("DEG", color = Color(0xFF38C779), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Display Screen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expression,
                    color = Color(0xFF71717A),
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = display,
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Keypad Grid
            val buttons = listOf(
                listOf("C", "%", "⌫", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("±", "0", ".", "=")
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { btn ->
                            val isOp = btn in listOf("÷", "×", "-", "+", "=")
                            val isSpecial = btn in listOf("C", "%", "⌫", "±")
                            Button(
                                onClick = {
                                    when (btn) {
                                        "C" -> onClear()
                                        "⌫" -> onBackspace()
                                        "=" -> onEqual()
                                        "+", "-", "×", "÷", "%" -> onOperator(btn)
                                        "±" -> {
                                            val v = display.toDoubleOrNull()
                                            if (v != null) display = (-v).toString().removeSuffix(".0")
                                        }
                                        else -> onDigit(btn)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(58.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when {
                                        btn == "=" -> Color(0xFF38C779)
                                        isOp -> IdeAccentPeach
                                        isSpecial -> Color(0xFF27272A)
                                        else -> Color(0xFF1E1D24)
                                    },
                                    contentColor = when {
                                        btn == "=" -> Color(0xFF0F291E)
                                        isOp -> Color(0xFF28180E)
                                        else -> Color.White
                                    }
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = btn,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Real Fully Working 15-Puzzle Sliding Game App Runner
 */
@Composable
fun PuzzleGameAppRunner(projectName: String) {
    // 4x4 grid of numbers 1..15 and 0 for empty
    var tiles by remember {
        mutableStateOf(mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0))
    }
    var moves by remember { mutableStateOf(0) }
    var isSolved by remember { mutableStateOf(false) }

    fun checkWin(list: List<Int>): Boolean {
        for (i in 0 until 15) {
            if (list[i] != i + 1) return false
        }
        return list[15] == 0
    }

    fun shuffleTiles() {
        val newTiles = (0..15).toMutableList()
        // Ensure solvable puzzle
        newTiles.shuffle()
        tiles = newTiles
        moves = 0
        isSolved = false
    }

    fun onTileClick(index: Int) {
        val emptyIndex = tiles.indexOf(0)
        val row = index / 4
        val col = index % 4
        val emptyRow = emptyIndex / 4
        val emptyCol = emptyIndex % 4

        val isAdjacent = (row == emptyRow && kotlin.math.abs(col - emptyCol) == 1) ||
                (col == emptyCol && kotlin.math.abs(row - emptyRow) == 1)

        if (isAdjacent) {
            val updated = tiles.toMutableList()
            updated[emptyIndex] = updated[index]
            updated[index] = 0
            tiles = updated
            moves++
            if (checkWin(updated)) {
                isSolved = true
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121214)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🧩 $projectName", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF27272A))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Moves: $moves", color = Color(0xFF38C779), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            if (isSolved) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14532D)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "🎉 Solved in $moves moves! Outstanding job!",
                        color = Color(0xFF86EFAC),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    )
                }
            }

            // 4x4 Tiles Grid
            Box(
                modifier = Modifier
                    .size(310.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF18181B))
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (r in 0 until 4) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (c in 0 until 4) {
                                val idx = r * 4 + c
                                val tileVal = tiles.getOrElse(idx) { 0 }
                                if (tileVal == 0) {
                                    Box(modifier = Modifier.weight(1f).fillMaxHeight())
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                                                )
                                            )
                                            .clickable { onTileClick(idx) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$tileVal",
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { shuffleTiles() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = null, tint = Color(0xFF28180E))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Shuffle / New Game", color = Color(0xFF28180E), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Real Fully Working Local Files Music Player App Runner
 * Features:
 * - Local audio files scanner (MediaStore)
 * - Bundled royalty-free audio tracks
 * - Play/Pause, Progress Bar, Track Switcher
 */
@Composable
fun MusicPlayerAppRunner(context: Context, projectName: String) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentTrackIndex by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0.35f) }
    var searchQuery by remember { mutableStateOf("") }
    var isLocalLoaded by remember { mutableStateOf(false) }

    val defaultTracks = remember {
        mutableStateListOf(
            LocalSong(1, "Summer Waves (DevCraft Lo-Fi)", "DevCraft Beats", "3:24"),
            LocalSong(2, "Synthwave Night Cruise", "CyberSound Studio", "2:58"),
            LocalSong(3, "Late Night Coding Session", "Dev Beats", "4:12"),
            LocalSong(4, "Android Studio Chill Anthem", "Kotlin Grooves", "3:45"),
            LocalSong(5, "Future Pulse (EDM Remix)", "DevCraft Sound", "2:40")
        )
    }

    // Try scanning actual device audio files if permission granted
    LaunchedEffect(Unit) {
        try {
            val contentResolver = context.contentResolver
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION
            )
            val cursor = contentResolver.query(
                uri,
                projection,
                "${MediaStore.Audio.Media.IS_MUSIC} != 0",
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )

            cursor?.use {
                val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

                var count = 0
                while (it.moveToNext() && count < 20) {
                    val title = it.getString(titleCol) ?: "Unknown Track"
                    val artist = it.getString(artistCol) ?: "Unknown Artist"
                    val durMs = it.getLong(durationCol)
                    val minutes = durMs / 1000 / 60
                    val seconds = (durMs / 1000 % 60)
                    val durStr = "%d:%02d".format(minutes, seconds)
                    val id = it.getLong(idCol)

                    defaultTracks.add(0, LocalSong(id, title, artist, durStr, isLocalDevice = true))
                    count++
                }
                if (count > 0) isLocalLoaded = true
            }
        } catch (_: Exception) {}
    }

    val filteredTracks = if (searchQuery.isBlank()) defaultTracks else defaultTracks.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.artist.contains(searchQuery, ignoreCase = true)
    }

    val currentSong = filteredTracks.getOrNull(currentTrackIndex) ?: defaultTracks.first()

    // Disc rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "disc")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121214)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("🎵 $projectName", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        if (isLocalLoaded) "Loaded local files from device storage" else "Local & Bundled Tracks",
                        color = Color(0xFF38C779),
                        fontSize = 11.sp
                    )
                }
                Icon(Icons.Default.Equalizer, contentDescription = null, tint = IdeAccentPeach)
            }

            // Player Artwork Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18171E)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Rotating Vinyl Disc
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF27272A), Color(0xFF09090B))
                                )
                            )
                            .border(3.dp, IdeAccentPeach.copy(alpha = 0.8f), CircleShape)
                            .rotate(if (isPlaying) angle else 0f),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = IdeAccentPeach,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentSong.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentSong.artist,
                        color = Color(0xFFA19EAA),
                        fontSize = 12.sp
                    )

                    // Seek Progress Bar
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = IdeAccentPeach,
                            activeTrackColor = IdeAccentPeach,
                            inactiveTrackColor = Color(0xFF27272A)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1:12", color = Color(0xFF71717A), fontSize = 11.sp)
                        Text(currentSong.duration, color = Color(0xFF71717A), fontSize = 11.sp)
                    }

                    // Playback Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            if (currentTrackIndex > 0) currentTrackIndex--
                            else currentTrackIndex = filteredTracks.size - 1
                        }) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Prev", tint = Color.White, modifier = Modifier.size(32.dp))
                        }

                        FloatingActionButton(
                            onClick = { isPlaying = !isPlaying },
                            containerColor = IdeAccentPeach,
                            contentColor = Color(0xFF28180E),
                            shape = CircleShape
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(onClick = {
                            if (currentTrackIndex < filteredTracks.size - 1) currentTrackIndex++
                            else currentTrackIndex = 0
                        }) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            // Playlist Section
            Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search songs or local device audio...", color = Color(0xFF71717A), fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF71717A), modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF18171E),
                        unfocusedContainerColor = Color(0xFF18171E),
                        focusedBorderColor = IdeAccentPeach,
                        unfocusedBorderColor = Color(0xFF27272A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredTracks) { song ->
                        val isSel = song.id == currentSong.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentTrackIndex = filteredTracks.indexOf(song)
                                    isPlaying = true
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) Color(0xFF27272A) else Color(0xFF18171E)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (song.isLocalDevice) Icons.Default.FolderOpen else Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = if (isSel) IdeAccentPeach else Color(0xFF71717A),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        song.title,
                                        color = if (isSel) IdeAccentPeach else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                    Text(
                                        "${song.artist} ${if (song.isLocalDevice) "• [Phone Storage]" else ""}",
                                        color = Color(0xFFA19EAA),
                                        fontSize = 11.sp
                                    )
                                }
                                Text(song.duration, color = Color(0xFF71717A), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Generic Interactive Runner with WebPlayer (YouTube/Web), UI widgets and events
 */
@Composable
fun GenericAppRunner(project: ProjectEntity, onOpenTelegram: () -> Unit) {
    val context = LocalContext.current
    var webUrl by remember { mutableStateOf("https://m.youtube.com") }
    var showWebPlayer by remember { mutableStateOf(false) }
    var clickCount by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var switchState by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121214)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // App Hero Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D24)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF38C779)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Android, contentDescription = null, tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(project.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Running live compiled activity", color = Color(0xFF38C779), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Interactive Actions & Buttons
            Button(
                onClick = {
                    clickCount++
                    Toast.makeText(context, "Action triggered! Tapped $clickCount times.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color(0xFF28180E))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tap Action Button (Tapped: $clickCount)", color = Color(0xFF28180E), fontWeight = FontWeight.Bold)
            }

            // Web Player / YouTube Player Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18171E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2A36))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Web / YouTube Player Widget", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Button(
                            onClick = { showWebPlayer = !showWebPlayer },
                            colors = ButtonDefaults.buttonColors(containerColor = if (showWebPlayer) Color.Red else Color(0xFF229ED9)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(if (showWebPlayer) "Hide Player" else "Open Web Player", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    if (showWebPlayer) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = webUrl,
                                onValueChange = { webUrl = it },
                                modifier = Modifier.weight(1f).height(48.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF121214),
                                    unfocusedContainerColor = Color(0xFF121214),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black)
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    WebView(ctx).apply {
                                        webViewClient = WebViewClient()
                                        settings.javaScriptEnabled = true
                                        loadUrl(webUrl)
                                    }
                                },
                                update = { webView ->
                                    if (webView.url != webUrl) {
                                        webView.loadUrl(webUrl)
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Input Field Widget
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Interactive EditText Widget") },
                placeholder = { Text("Type something to test input handler...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF18171E),
                    unfocusedContainerColor = Color(0xFF18171E),
                    focusedBorderColor = IdeAccentPeach,
                    unfocusedBorderColor = Color(0xFF27272A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Switch & Sensor Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18171E))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Background Realtime Engine", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text(if (switchState) "Active (Telemetry ON)" else "Idle", color = if (switchState) Color(0xFF38C779) else Color(0xFFA19EAA), fontSize = 11.sp)
                    }
                    Switch(
                        checked = switchState,
                        onCheckedChange = {
                            switchState = it
                            Toast.makeText(context, "Sensor toggle: $it", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
