package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IdeAccentPeach
import com.example.ui.theme.IdeTextPrimary
import com.example.ui.theme.IdeTextSecondary

@Composable
fun IdeHeader(
    modifier: Modifier = Modifier,
    showBadge: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Android Studio",
            color = IdeTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Your Ideas, Anywhere",
            color = IdeTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal
        )
        if (showBadge) {
            Spacer(modifier = Modifier.height(18.dp))
            AcsLogoBadge()
        }
    }
}

@Composable
fun AcsLogoBadge(
    modifier: Modifier = Modifier
) {
    // Hexagonal / Rounded badge with tan border and dark center as in screenshot
    Box(
        modifier = modifier
            .size(68.dp)
            .background(Color(0xFFDCA683).copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .border(2.dp, Color(0xFFDCA683).copy(alpha = 0.8f), RoundedCornerShape(20.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1D24), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFE5484D).copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ACS",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "< • >",
                    color = IdeAccentPeach,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
