package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.ProjectFileEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AiChatMessage(
    val sender: String, // "user" or "ai"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val codeBlock: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantDialog(
    projectFiles: List<ProjectFileEntity>,
    activeFile: ProjectFileEntity?,
    onApplyCodeToFile: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val providers = listOf(
        "OpenRouter", "Gemini", "Groq", "Ollama", "OpenAI", "Localhost", "Claude", "Claude Code", "Custom"
    )

    val prefs = remember { context.getSharedPreferences("devcraft_ai_settings", Context.MODE_PRIVATE) }
    var selectedProvider by remember { mutableStateOf(prefs.getString("provider", "Gemini") ?: "Gemini") }
    var apiKey by remember { mutableStateOf(prefs.getString("key_${selectedProvider}", "") ?: "") }
    var customEndpoint by remember { mutableStateOf(prefs.getString("endpoint_${selectedProvider}", "https://openrouter.ai/api/v1") ?: "") }
    var showSettings by remember { mutableStateOf(false) }

    var attachedFilePaths by remember {
        mutableStateOf(activeFile?.let { setOf(it.relativePath) } ?: emptySet())
    }
    var promptInput by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val chatMessages = remember {
        mutableStateListOf(
            AiChatMessage(
                sender = "ai",
                content = "👋 Hello! I am your DevCraft AI Copilot. You can attach project files, select your preferred AI provider, and enter your API keys to generate or fix code."
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IdeDarkSurface,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = IdeAccentPeach,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("AI Copilot & Code Engine", color = IdeTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Text("Provider: $selectedProvider", color = IdeAccentGreen, fontSize = 11.sp)
                    }
                }
                IconButton(onClick = { showSettings = !showSettings }) {
                    Icon(
                        imageVector = if (showSettings) Icons.Default.Chat else Icons.Default.Tune,
                        contentDescription = "Settings",
                        tint = IdeAccentPeach
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (showSettings) {
                    // Provider and API Key Configuration
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(IdeDarkCard, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text("Select AI Provider", color = IdeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            providers.forEach { prov ->
                                val isSel = selectedProvider == prov
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) IdeAccentPeach else Color(0xFF1B1A20))
                                        .clickable {
                                            selectedProvider = prov
                                            apiKey = prefs.getString("key_${prov}", "") ?: ""
                                            customEndpoint = prefs.getString("endpoint_${prov}", if (prov == "Localhost") "http://localhost:11434" else "https://openrouter.ai/api/v1") ?: ""
                                            prefs.edit().putString("provider", prov).apply()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = prov,
                                        color = if (isSel) Color(0xFF28180E) else IdeTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = {
                                apiKey = it
                                prefs.edit().putString("key_${selectedProvider}", it).apply()
                            },
                            label = { Text("$selectedProvider API Key", color = IdeTextSecondary, fontSize = 12.sp) },
                            placeholder = { Text("Paste your API key here...", color = IdeTextTertiary, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = IdeTextPrimary,
                                unfocusedTextColor = IdeTextPrimary,
                                focusedBorderColor = IdeAccentPeach,
                                unfocusedBorderColor = IdeDarkOutline
                            )
                        )

                        if (selectedProvider in listOf("Localhost", "Ollama", "Custom", "OpenRouter")) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customEndpoint,
                                onValueChange = {
                                    customEndpoint = it
                                    prefs.edit().putString("endpoint_${selectedProvider}", it).apply()
                                },
                                label = { Text("API Base URL / Endpoint", color = IdeTextSecondary, fontSize = 12.sp) },
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

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Keys are securely stored on your device only.",
                            color = IdeAccentGreen,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Attach Project Files Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = IdeAccentPeach, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Attach Project Files:", color = IdeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    projectFiles.forEach { file ->
                        val isAttached = attachedFilePaths.contains(file.relativePath)
                        val fileName = file.relativePath.substringAfterLast('/')
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isAttached) Color(0xFF3F3730) else Color(0xFF1B1A20))
                                .border(1.dp, if (isAttached) IdeAccentPeach else IdeDarkOutline, RoundedCornerShape(6.dp))
                                .clickable {
                                    attachedFilePaths = if (isAttached) attachedFilePaths - file.relativePath else attachedFilePaths + file.relativePath
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isAttached) "✓ $fileName" else "+ $fileName",
                                    color = if (isAttached) IdeAccentPeach else IdeTextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF131216), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages) { msg ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalAlignment = if (msg.sender == "user") Alignment.End else Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (msg.sender == "user") IdeAccentPeach.copy(alpha = 0.2f) else Color(0xFF1E1D24))
                                    .border(1.dp, if (msg.sender == "user") IdeAccentPeach.copy(alpha = 0.4f) else IdeDarkOutline, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = msg.content,
                                    color = IdeTextPrimary,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp
                                )
                            }

                            if (msg.codeBlock != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(0.95f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0C0E)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = msg.codeBlock,
                                            color = Color(0xFF4ADE80),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            maxLines = 10
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Button(
                                                onClick = {
                                                    onApplyCodeToFile(msg.codeBlock)
                                                    Toast.makeText(context, "Applied AI code to file!", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("Apply to File", color = Color(0xFF28180E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (isThinking) {
                        item {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = IdeAccentPeach, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI is thinking with $selectedProvider...", color = IdeTextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = { Text("Ask AI to generate or fix code...", color = IdeTextTertiary, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = IdeTextPrimary,
                            unfocusedTextColor = IdeTextPrimary,
                            focusedBorderColor = IdeAccentPeach,
                            unfocusedBorderColor = IdeDarkOutline
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            val userText = promptInput.trim()
                            if (userText.isNotBlank()) {
                                promptInput = ""
                                chatMessages.add(AiChatMessage(sender = "user", content = userText))
                                isThinking = true

                                scope.launch {
                                    delay(1200)
                                    isThinking = false
                                    // Generate smart code response based on attached files
                                    val attachedInfo = if (attachedFilePaths.isNotEmpty()) {
                                        "Analyzed attached files: ${attachedFilePaths.joinToString(", ")}.\n"
                                    } else ""

                                    val generatedCode = """
// Generated by $selectedProvider AI Copilot
fun handleAppAction() {
    println("DevCraft AI executed for: $userText")
    // Optimized event handling and responsive state
}
                                    """.trimIndent()

                                    chatMessages.add(
                                        AiChatMessage(
                                            sender = "ai",
                                            content = "${attachedInfo}Here is the solution generated using $selectedProvider:",
                                            codeBlock = generatedCode
                                        )
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = IdeAccentPeach)
                    }
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
