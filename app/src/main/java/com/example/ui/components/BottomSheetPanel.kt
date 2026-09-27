package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.BottomTab

@Composable
fun BottomSheetPanel(
    activeTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    // Build output props
    buildLogs: List<String>,
    isBuilding: Boolean,
    onInstallApk: () -> Unit,
    onShareApk: () -> Unit,
    onClearBuildLogs: () -> Unit,
    // Terminal props
    hasTerminalSession: Boolean,
    terminalLogs: String,
    terminalInput: String,
    onTerminalInputChange: (String) -> Unit,
    onTerminalKeyClick: (String) -> Unit,
    onExecuteCommand: () -> Unit,
    onInitTerminal: () -> Unit,
    // Other tabs
    appLogs: List<String>,
    ideLogs: List<String>,
    diagnostics: List<String>,
    onCloseSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF131216))
    ) {
        // Tab Header matching Screenshots 6 & 7
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B1A20))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabHeaderButton("Build output", activeTab == BottomTab.BUILD_OUTPUT) {
                onTabSelected(BottomTab.BUILD_OUTPUT)
            }
            TabHeaderButton("App Logs", activeTab == BottomTab.APP_LOGS) {
                onTabSelected(BottomTab.APP_LOGS)
            }
            TabHeaderButton("Terminal", activeTab == BottomTab.TERMINAL) {
                onTabSelected(BottomTab.TERMINAL)
            }
            TabHeaderButton("IDE Logs", activeTab == BottomTab.IDE_LOGS) {
                onTabSelected(BottomTab.IDE_LOGS)
            }
            TabHeaderButton("Diagnostics", activeTab == BottomTab.DIAGNOSTICS) {
                onTabSelected(BottomTab.DIAGNOSTICS)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onCloseSheet, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Collapse",
                    tint = IdeTextSecondary
                )
            }
        }

        HorizontalDivider(color = IdeDarkOutline, thickness = 1.dp)

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (activeTab) {
                BottomTab.BUILD_OUTPUT -> {
                    BuildOutputView(
                        logs = buildLogs,
                        isBuilding = isBuilding,
                        onInstallApk = onInstallApk,
                        onShareApk = onShareApk,
                        onClear = onClearBuildLogs
                    )
                }
                BottomTab.TERMINAL -> {
                    TerminalView(
                        hasSession = hasTerminalSession,
                        logs = terminalLogs,
                        input = terminalInput,
                        onInputChange = onTerminalInputChange,
                        onKeyClick = onTerminalKeyClick,
                        onExecute = onExecuteCommand,
                        onInit = onInitTerminal
                    )
                }
                BottomTab.APP_LOGS -> {
                    LogListView(logs = appLogs, emptyMessage = "No app logs yet.")
                }
                BottomTab.IDE_LOGS -> {
                    LogListView(logs = ideLogs, emptyMessage = "No IDE logs.")
                }
                BottomTab.DIAGNOSTICS -> {
                    DiagnosticsView(diagnostics = diagnostics)
                }
            }
        }
    }
}

@Composable
fun TabHeaderButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = if (isSelected) IdeAccentPeach else IdeTextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(IdeAccentPeach)
            )
        }
    }
}

@Composable
fun BuildOutputView(
    logs: List<String>,
    isBuilding: Boolean,
    onInstallApk: () -> Unit,
    onShareApk: () -> Unit,
    onClear: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF131216))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            itemsIndexed(logs) { index, line ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "${index + 1} ",
                        color = IdeTextTertiary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(28.dp)
                    )
                    Text(
                        text = line,
                        color = when {
                            line.contains("SUCCESSFUL") || line.contains("ready") -> IdeAccentGreen
                            line.contains("ERROR") || line.contains("FAILED") -> IdeAccentRed
                            line.contains("Starting") -> IdeAccentPeach
                            else -> IdeTextPrimary
                        },
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            if (isBuilding) {
                item {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = IdeAccentPeach,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compiling and packaging...",
                            color = IdeAccentPeach,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }

        // Floating Action buttons on bottom-right matching Screenshot 7
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Install APK Direct button
            FloatingActionButton(
                onClick = onInstallApk,
                containerColor = IdeAccentGreen,
                contentColor = Color(0xFF00391D),
                modifier = Modifier.size(46.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Download, contentDescription = "Install APK", modifier = Modifier.size(22.dp))
            }

            // Share APK button
            FloatingActionButton(
                onClick = onShareApk,
                containerColor = Color(0xFF5A4434),
                contentColor = IdeAccentPeach,
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share APK", modifier = Modifier.size(20.dp))
            }

            // Delete / Clear button
            FloatingActionButton(
                onClick = onClear,
                containerColor = Color(0xFF5A4434),
                contentColor = IdeAccentPeach,
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Clear logs", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun TerminalView(
    hasSession: Boolean,
    logs: String,
    input: String,
    onInputChange: (String) -> Unit,
    onKeyClick: (String) -> Unit,
    onExecute: () -> Unit,
    onInit: () -> Unit
) {
    if (!hasSession) {
        // "No Terminal Session" card matching Screenshot 6
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = CardDefaults.cardColors(containerColor = IdeDarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = IdeTextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Terminal Session",
                        color = IdeTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Create your first terminal session",
                        color = IdeTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onInit,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A4434)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = IdeAccentPeach, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("+ Initialize Terminal", color = IdeAccentPeach)
                    }
                }
            }
        }
    } else {
        // Active Terminal Session matching Screenshot 6
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TerminalBackground)
        ) {
            // Sub-header: Hamburger icon, Session 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1B1A20))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = IdeTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Session 1",
                    color = IdeTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Terminal Console Body
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(8.dp)
            ) {
                Text(
                    text = logs,
                    color = TerminalText,
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }

            // Command Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1920))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$ ",
                    color = TerminalGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    placeholder = { Text("type command (ls, python, gradle, git)...", color = IdeTextTertiary, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                    singleLine = true
                )
                IconButton(onClick = onExecute) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Execute",
                        tint = IdeAccentPeach,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Virtual Terminal Keys matching Screenshot 6 exactly
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141318))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Row 1: ESC, TAB, CTRL, ALT, /
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("ESC", "TAB", "CTRL", "ALT", "/").forEach { key ->
                        VirtualKeyButton(key = key, onClick = { onKeyClick(key) }, modifier = Modifier.weight(1f))
                    }
                }
                // Row 2: ↑, ↓, ←, →, HOME
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("↑", "↓", "←", "→", "HOME").forEach { key ->
                        VirtualKeyButton(key = key, onClick = { onKeyClick(key) }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun VirtualKeyButton(
    key: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 3.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF24222D))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key,
            color = IdeTextPrimary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LogListView(logs: List<String>, emptyMessage: String) {
    if (logs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = emptyMessage, color = IdeTextSecondary, fontSize = 13.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF131216))
                .padding(12.dp)
        ) {
            items(logs.size) { i ->
                Text(
                    text = logs[i],
                    color = IdeTextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun DiagnosticsView(diagnostics: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF131216))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "System Toolchains & SDK Status",
                color = IdeTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
        items(diagnostics.size) { i ->
            Card(
                colors = CardDefaults.cardColors(containerColor = IdeDarkSurface),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = diagnostics[i],
                        color = IdeAccentGreen,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
