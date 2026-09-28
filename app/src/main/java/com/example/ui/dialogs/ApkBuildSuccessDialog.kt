package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ApkBuildResult
import com.example.data.model.ProjectEntity
import com.example.ui.screens.StandaloneAppActivity
import com.example.ui.theme.*

@Composable
fun ApkBuildSuccessDialog(
    buildResult: ApkBuildResult,
    project: ProjectEntity,
    onDirectRun: () -> Unit,
    onInstallApk: () -> Unit,
    onShareApk: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IdeDarkSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(IdeAccentGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = IdeAccentGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "APK Generated Successfully!",
                        color = IdeTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Unique standalone bundle created for this project",
                        color = IdeAccentGreen,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Icon Card Preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = IdeDarkCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Generated Icon
                        if (buildResult.iconBitmap != null) {
                            Image(
                                bitmap = buildResult.iconBitmap.asImageBitmap(),
                                contentDescription = "App Icon",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.5.dp, IdeAccentPeach.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(IdeAccentGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Android, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = buildResult.projectName,
                                color = IdeTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = buildResult.packageName,
                                color = IdeTextTertiary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1E293B)
                                ) {
                                    Text(
                                        text = "v${buildResult.versionName} (${buildResult.versionCode})",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF27252F)
                                ) {
                                    Text(
                                        text = "${buildResult.fileSizeBytes / 1024} KB",
                                        color = IdeAccentPeach,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Permissions Baked into APK
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16151C))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "CONFIGURED PROJECT PERMISSIONS",
                            color = IdeTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (buildResult.permissionsIncluded.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                buildResult.permissionsIncluded.take(3).forEach { perm ->
                                    val simple = perm.substringAfterLast('.')
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF1E2822)
                                    ) {
                                        Text(
                                            text = "✓ $simple",
                                            color = IdeAccentGreen,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(text = "Standard Android permissions", color = IdeTextTertiary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Output File Location
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF191820))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = IdeTextTertiary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = buildResult.apkFile.name,
                        color = IdeTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("APK Path", buildResult.apkFile.absolutePath))
                            Toast.makeText(context, "Copied APK path to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Path", tint = IdeAccentPeach, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action 1: Instant Standalone Launch (Works 100% without system installer permissions)
                Button(
                    onClick = {
                        onDismiss()
                        val intent = Intent(context, StandaloneAppActivity::class.java).apply {
                            putExtra("EXTRA_PROJECT_ID", project.id)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IdeAccentGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF0F291E))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Launch Project App (Instant Run)", color = Color(0xFF0F291E), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action 2: System Package Installer
                Button(
                    onClick = {
                        onDismiss()
                        onInstallApk()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IdeAccentPeach),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.InstallMobile, contentDescription = null, tint = Color(0xFF28180E))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Install via Package Installer", color = Color(0xFF28180E), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action 3: Share / Export APK
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onShareApk()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = IdeTextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IdeDarkOutline)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = IdeTextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share / Export APK", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = IdeTextSecondary)
            }
        }
    )
}
