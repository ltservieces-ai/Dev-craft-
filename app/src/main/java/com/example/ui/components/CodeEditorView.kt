package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectFileEntity
import com.example.ui.theme.*

@Composable
fun CodeEditorView(
    currentFile: ProjectFileEntity,
    code: String,
    openTabs: List<String>,
    onTabSelected: (String) -> Unit,
    onTabClosed: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    fontSize: Int = 14,
    modifier: Modifier = Modifier
) {
    val quickSymbols = listOf("{", "}", "(", ")", "[", "]", ";", "\"", "'", "=", ".", ":", "->", "fun", "val", "def", "class", "import")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF141318))
    ) {
        // Multi-tab bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1920))
                .horizontalScroll(rememberScrollState())
        ) {
            openTabs.forEach { tabPath ->
                val fileName = tabPath.substringAfterLast('/')
                val isSelected = tabPath == currentFile.relativePath

                Row(
                    modifier = Modifier
                        .background(
                            if (isSelected) Color(0xFF222129) else Color(0xFF1A1920)
                        )
                        .clickable { onTabSelected(tabPath) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fileName,
                        color = if (isSelected) IdeAccentPeach else IdeTextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close tab",
                        tint = IdeTextTertiary,
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { onTabClosed(tabPath) }
                    )
                }
            }
        }

        HorizontalDivider(color = IdeDarkOutline, thickness = 1.dp)

        // Line Numbers + Editor Body
        val lineCount = remember(code) { code.lines().size.coerceAtLeast(1) }
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Line numbers gutter
                Column(
                    modifier = Modifier
                        .background(Color(0xFF111015))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..lineCount) {
                        Text(
                            text = "$i",
                            color = IdeTextTertiary,
                            fontSize = fontSize.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = (fontSize * 1.45).sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Editable Code
                BasicTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    textStyle = TextStyle(
                        color = IdeTextPrimary,
                        fontSize = fontSize.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = (fontSize * 1.45).sp
                    ),
                    cursorBrush = SolidColor(IdeAccentPeach),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 6.dp, horizontal = 4.dp)
                )
            }
        }

        // Quick developer symbol keyboard bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1D24))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickSymbols.forEach { sym ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFF2C2A36), RoundedCornerShape(6.dp))
                        .clickable { onCodeChange(code + sym) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sym,
                        color = IdeTextPrimary,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
