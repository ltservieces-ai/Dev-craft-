package com.example.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import com.example.data.AppDatabase
import com.example.data.model.ProjectEntity
import com.example.data.repository.ProjectRepository
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class StandaloneAppActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val projectId = intent.getLongExtra("EXTRA_PROJECT_ID", -1L)

        setContent {
            MyApplicationTheme {
                val repository = remember { ProjectRepository(AppDatabase.getDatabase(applicationContext)) }
                val coroutineScope = rememberCoroutineScope()
                var project by remember { mutableStateOf<ProjectEntity?>(null) }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(projectId) {
                    val proj = if (projectId != -1L) {
                        repository.getProjectDirect(projectId)
                    } else {
                        repository.allProjects.firstOrNull()?.firstOrNull()
                    }
                    project = proj
                    isLoading = false
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    val p = project
                    if (p != null) {
                        AppRunnerScreen(
                            project = p,
                            onBack = { finish() },
                            onInstallApk = {
                                coroutineScope.launch {
                                    val files = repository.getFiles(p.id).firstOrNull() ?: emptyList()
                                    val result = repository.buildProjectApk(p, files, this@StandaloneAppActivity)
                                    val apk = result.apkFile
                                    if (apk.exists()) {
                                        try {
                                            val uri = FileProvider.getUriForFile(
                                                this@StandaloneAppActivity,
                                                "${packageName}.provider",
                                                apk
                                            )
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(uri, "application/vnd.android.package-archive")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(this@StandaloneAppActivity, "Cannot open installer: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            onOpenSupportDeveloper = {
                                Toast.makeText(this@StandaloneAppActivity, "DevCraft Standalone Engine v2.4", Toast.LENGTH_SHORT).show()
                            },
                            onOpenTelegram = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://t.me/devcraftupdates"))
                                    startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Project not found",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
