package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class TemplateItem(
    val id: String,
    val name: String,
    val language: String,
    val description: String,
    val previewType: String // "no_activity", "basic", "empty", "compose", "bottom_nav", "drawer", "python", "html", "c"
)

@Composable
fun ChooseTemplateScreen(
    onTemplateSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val templates = listOf(
        TemplateItem("no_activity", "No Activity", "Kotlin/Java", "Template without any pre-configured Activity", "no_activity"),
        TemplateItem("music_app", "Music App", "Kotlin", "Full audio player, playback controls & playlist", "music"),
        TemplateItem("calculator", "Calculator", "Kotlin", "Modern interactive math calculator with display", "calculator"),
        TemplateItem("puzzle_game", "Puzzle Game", "Kotlin", "Interactive 15-sliding tile puzzle game", "puzzle"),
        TemplateItem("basic", "Basic Activity", "Kotlin", "Standard toolbar and floating action button", "basic"),
        TemplateItem("empty", "Empty Activity", "Kotlin", "Standard single empty activity", "empty"),
        TemplateItem("compose", "Compose Activity", "Kotlin", "Modern Jetpack Compose activity with declarative UI", "compose"),
        TemplateItem("bottom_nav", "Bottom Navigation", "Kotlin", "Activity with bottom navigation bar", "bottom_nav"),
        TemplateItem("drawer", "Navigation drawer", "Kotlin", "Activity with side navigation drawer", "drawer"),
        TemplateItem("python", "Python App", "Python", "Python 3.11 mobile application with Kivy runtime", "python"),
        TemplateItem("html", "HTML5 App", "HTML", "Mobile hybrid Web App with HTML, CSS, and JS", "html"),
        TemplateItem("c", "C Native Core", "C", "C / C++ Native JNI shared library with CMake", "c")
    )

    Scaffold(
        containerColor = IdeDarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = IdeTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Choose Template",
                    color = IdeTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(IdeDarkSurface)
                .padding(16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(templates) { item ->
                    TemplateCard(item = item, onClick = { onTemplateSelected(item.name) })
                }
            }
        }
    }
}

@Composable
fun TemplateCard(
    item: TemplateItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF282631)),
        border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.name,
                color = IdeTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1B1A22))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                TemplateVisualPreview(previewType = item.previewType)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.language,
                color = IdeAccentPeach,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TemplateVisualPreview(previewType: String) {
    when (previewType) {
        "no_activity" -> {
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(100.dp)
                    .border(
                        width = 1.5.dp,
                        color = Color.Gray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
        "compose" -> {
            // White phone mockup with green header and 3D Compose Cube in center
            Column(
                modifier = Modifier
                    .width(74.dp)
                    .height(105.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
            ) {
                // Green toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .background(Color(0xFF00C853))
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).background(Color.White, CircleShape))
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(3.dp).background(Color.White, CircleShape))
                }
                // Center Compose 3D isometric cube
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF1B3858), RoundedCornerShape(4.dp))
                            .border(2.dp, Color(0xFF4285F4), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Color(0xFF34A853), RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
        "basic" -> {
            Column(
                modifier = Modifier
                    .width(74.dp)
                    .height(105.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .background(Color(0xFF00C853))
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).background(Color.White, CircleShape))
                }
                Box(
                    modifier = Modifier.fillMaxSize().padding(6.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFF00C853), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
        "empty" -> {
            Column(
                modifier = Modifier
                    .width(74.dp)
                    .height(105.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .background(Color(0xFF00C853))
                )
            }
        }
        "music" -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🎵", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Music Player", color = IdeAccentPeach, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        "calculator" -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🧮", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Calculator", color = IdeAccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        "puzzle" -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🧩", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "15-Puzzle", color = IdeAccentCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        "python" -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🐍", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Python 3", color = Color(0xFFECC94B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        "html" -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🌐", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "HTML5 / JS", color = Color(0xFFE5484D), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        "c" -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "⚙️", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "C / C++ NDK", color = Color(0xFF60A5FA), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .width(74.dp)
                    .height(105.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .background(Color(0xFF00C853))
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(Color(0xFFE0E0E0))
                )
            }
        }
    }
}
