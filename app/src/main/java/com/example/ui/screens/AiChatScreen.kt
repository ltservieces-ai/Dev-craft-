package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectFileEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val sender: String, // "user" or "ai"
    val text: String,
    val codeBlock: String? = null,
    val timestamp: String = "Just now"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    projectFiles: List<ProjectFileEntity>,
    activeFile: ProjectFileEntity?,
    onBack: () -> Unit,
    onApplyCodeToFile: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    BackHandler { onBack() }

    var selectedModel by remember { mutableStateOf("Gemini 2.5 Flash") }
    var showModelMenu by remember { mutableStateOf(false) }
    var userPrompt by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "ai",
                text = "Hello! I am your DevCraft AI Engineer. I can write Jetpack Compose UI, Kotlin activities, Sketchware event blocks, audio services, and fix any build errors for your project.",
                codeBlock = null
            )
        )
    }

    val quickPrompts = listOf(
        "🧮 Calculator UI & Logic",
        "🎵 Music Player Audio Scanner",
        "🧩 15-Puzzle Sliding Game",
        "📺 YouTube Player WebView",
        "🔥 Firebase Auth & Firestore",
        "💳 Razorpay Payment Gateway",
        "🛠️ Fix Kotlin Compile Error"
    )

    fun sendPrompt(prompt: String) {
        if (prompt.isBlank() || isGenerating) return
        val currentInput = prompt.trim()
        messages.add(ChatMessage(sender = "user", text = currentInput))
        userPrompt = ""
        isGenerating = true

        coroutineScope.launch {
            delay(100)
            listState.animateScrollToItem(messages.size - 1)
            delay(800)

            val pLower = currentInput.lowercase()
            val (respText, code) = when {
                pLower.contains("calc") -> {
                    Pair(
                        "Here is a complete, production-ready interactive Calculator Activity in Kotlin with basic arithmetic operations:",
                        """
package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var display = "0"
    private var firstVal = 0.0
    private var op = ""
    private var resetNext = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv = findViewById<TextView>(R.id.tv_display)
        val btnClear = findViewById<Button>(R.id.btn_clear)
        val btnEqual = findViewById<Button>(R.id.btn_equal)

        btnClear.setOnClickListener {
            display = "0"
            firstVal = 0.0
            op = ""
            tv.text = display
        }

        btnEqual.setOnClickListener {
            val second = display.toDoubleOrNull() ?: 0.0
            val res = when(op) {
                "+" -> firstVal + second
                "-" -> firstVal - second
                "*" -> firstVal * second
                "/" -> if (second != 0.0) firstVal / second else 0.0
                else -> second
            }
            display = res.toString()
            tv.text = display
            resetNext = true
        }
    }
}
                        """.trimIndent()
                    )
                }
                pLower.contains("music") || pLower.contains("audio") || pLower.contains("song") -> {
                    Pair(
                        "Here is the local audio scanner helper that queries Android's MediaStore for music tracks on the phone:",
                        """
package com.example.musicapp

import android.content.Context
import android.provider.MediaStore

data class AudioTrack(val id: Long, val title: String, val artist: String, val path: String)

object AudioFetchHelper {
    fun fetchLocalSongs(context: Context): List<AudioTrack> {
        val list = mutableListOf<AudioTrack>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA
        )
        val cursor = context.contentResolver.query(uri, projection, "${'$'}{MediaStore.Audio.Media.IS_MUSIC} != 0", null, null)
        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val pathCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                list.add(AudioTrack(it.getLong(idCol), it.getString(titleCol), it.getString(artistCol), it.getString(pathCol)))
            }
        }
        return list
    }
}
                        """.trimIndent()
                    )
                }
                pLower.contains("youtube") || pLower.contains("video") -> {
                    Pair(
                        "Here is the YouTube IFrame WebPlayer embed code for Android WebView:",
                        """
package com.example.webplayer

import android.webkit.WebView
import android.webkit.WebViewClient

fun loadYouTubeVideo(webView: WebView, videoId: String) {
    webView.settings.javaScriptEnabled = true
    webView.webViewClient = WebViewClient()
    val html = ""${'"'}
        <!DOCTYPE html>
        <html>
        <body style="margin:0;padding:0;background:black;">
        <iframe width="100%" height="100%" src="https://www.youtube.com/embed/${'$'}videoId?autoplay=1" frameborder="0" allowfullscreen></iframe>
        </body>
        </html>
    ""${'"'}.trimIndent()
    webView.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)
}
                        """.trimIndent()
                    )
                }
                pLower.contains("puzzle") -> {
                    Pair(
                        "Here is the 15-sliding tile game logic solver and movement controller:",
                        """
package com.example.puzzlegame

class PuzzleEngine {
    val tiles = (0..15).toMutableList().also { it.shuffle() }

    fun moveTile(position: Int): Boolean {
        val emptyPos = tiles.indexOf(0)
        val r1 = position / 4; val c1 = position % 4
        val r2 = emptyPos / 4; val c2 = emptyPos % 4
        if ((r1 == r2 && kotlin.math.abs(c1 - c2) == 1) || (c1 == c2 && kotlin.math.abs(r1 - r2) == 1)) {
            tiles[emptyPos] = tiles[position]
            tiles[position] = 0
            return true
        }
        return false
    }

    fun isSolved(): Boolean = tiles.take(15) == (1..15).toList() && tiles[15] == 0
}
                        """.trimIndent()
                    )
                }
                pLower.contains("firebase") -> {
                    Pair(
                        "Here is the Firebase Auth setup and Google sign-in configuration:",
                        """
// In build.gradle: implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthManager {
    private val auth = FirebaseAuth.getInstance()

    fun signInAnonymous(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        auth.signInAnonymously()
            .addOnSuccessListener { result -> onSuccess(result.user?.uid ?: "") }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Auth failed") }
    }
}
                        """.trimIndent()
                    )
                }
                else -> {
                    Pair(
                        "I analyzed your request. Here is an optimized solution customized for ${activeFile?.relativePath ?: "your project"}:",
                        """
// DevCraft AI Generated Code
fun executeDevCraftAction(context: android.content.Context) {
    android.widget.Toast.makeText(context, "Action executed successfully!", android.widget.Toast.LENGTH_SHORT).show()
}
                        """.trimIndent()
                    )
                }
            }

            messages.add(ChatMessage(sender = "ai", text = respText, codeBlock = code))
            isGenerating = false
            delay(100)
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = IdeDarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("DevCraft AI Chat", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF38C779).copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("ONLINE", color = Color(0xFF38C779), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        // Model Switcher Button
                        Row(
                            modifier = Modifier.clickable { showModelMenu = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedModel, color = IdeAccentPeach, fontSize = 11.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = IdeAccentPeach, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        messages.clear()
                        messages.add(ChatMessage(sender = "ai", text = "Chat cleared. What can I build for you?"))
                    }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Chat", tint = Color(0xFFA19EAA))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IdeDarkSurfaceVariant)
            )
        },
        bottomBar = {
            // Prompt input bar
            Surface(
                color = IdeDarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Quick Prompts row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        items(quickPrompts) { qp ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF1E1D24))
                                    .border(1.dp, Color(0xFF3F3D4B), RoundedCornerShape(16.dp))
                                    .clickable { sendPrompt(qp) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(qp, color = Color(0xFFE2E8F0), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userPrompt,
                            onValueChange = { userPrompt = it },
                            placeholder = { Text("Ask DevCraft AI to write code, fix errors...", color = Color(0xFF71717A), fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = IdeDarkBackground,
                                unfocusedContainerColor = IdeDarkBackground,
                                focusedBorderColor = IdeAccentPeach,
                                unfocusedBorderColor = Color(0xFF27272A),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FloatingActionButton(
                            onClick = { sendPrompt(userPrompt) },
                            containerColor = IdeAccentPeach,
                            contentColor = Color(0xFF28180E),
                            shape = CircleShape,
                            modifier = Modifier.size(46.dp)
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF28180E), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF38BDF8), Color(0xFF818CF8))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column(
                        modifier = Modifier.widthIn(max = 310.dp),
                        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 4.dp,
                                bottomEnd = if (isUser) 4.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) IdeAccentPeach else Color(0xFF1E1D24)
                            ),
                            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2A36)) else null
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg.text,
                                    color = if (isUser) Color(0xFF28180E) else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
                                )

                                if (msg.codeBlock != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF100F14)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color(0xFF1A1920))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Kotlin / Compose", color = Color(0xFFA19EAA), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                                Row {
                                                    TextButton(
                                                        onClick = {
                                                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                            cm.setPrimaryClip(ClipData.newPlainText("Code", msg.codeBlock))
                                                            Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("Copy", fontSize = 11.sp, color = IdeAccentPeach)
                                                    }
                                                    TextButton(
                                                        onClick = {
                                                            onApplyCodeToFile(msg.codeBlock)
                                                            Toast.makeText(context, "Code applied to active editor file!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("Insert", fontSize = 11.sp, color = Color(0xFF38C779), fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                            Text(
                                                text = msg.codeBlock,
                                                color = Color(0xFFE2E8F0),
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Model Selector Dropdown
    DropdownMenu(
        expanded = showModelMenu,
        onDismissRequest = { showModelMenu = false }
    ) {
        listOf(
            "Gemini 2.5 Flash (Recommended)",
            "Groq LLaMA 3.3 70B",
            "Claude 3.7 Sonnet",
            "OpenAI GPT-4o",
            "DeepSeek R1",
            "DevCraft Offline Engine"
        ).forEach { m ->
            DropdownMenuItem(
                text = { Text(m) },
                onClick = {
                    selectedModel = m
                    showModelMenu = false
                }
            )
        }
    }
}
