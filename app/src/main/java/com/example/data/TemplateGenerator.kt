package com.example.data

import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectFileEntity

object TemplateGenerator {

    fun generateFilesForProject(project: ProjectEntity): List<ProjectFileEntity> {
        val files = mutableListOf<ProjectFileEntity>()
        val pkgPath = project.packageName.replace('.', '/')

        val manifestContent = generateAndroidManifest(project)
        files.add(
            ProjectFileEntity(
                projectId = project.id,
                relativePath = "app/src/main/AndroidManifest.xml",
                content = manifestContent
            )
        )

        val gradleContent = if (project.useKts) {
            """
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "${project.packageName}"
    compileSdk = 35

    defaultConfig {
        applicationId = "${project.packageName}"
        minSdk = 24
        targetSdk = 35
        versionCode = ${project.versionCode}
        versionName = "${project.versionName}"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
}
            """.trimIndent()
        } else {
            """
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace '${project.packageName}'
    compileSdk 35

    defaultConfig {
        applicationId '${project.packageName}'
        minSdk 24
        targetSdk 35
        versionCode ${project.versionCode}
        versionName '${project.versionName}'
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.13.1'
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
}
            """.trimIndent()
        }

        files.add(
            ProjectFileEntity(
                projectId = project.id,
                relativePath = if (project.useKts) "app/build.gradle.kts" else "app/build.gradle",
                content = gradleContent
            )
        )

        files.add(
            ProjectFileEntity(
                projectId = project.id,
                relativePath = "settings.gradle",
                content = "rootProject.name = \"${project.name}\"\ninclude(\":app\")"
            )
        )

        files.add(
            ProjectFileEntity(
                projectId = project.id,
                relativePath = "gradle.properties",
                content = "android.useAndroidX=true\nandroid.enableJetifier=true\norg.gradle.jvmargs=-Xmx2048m"
            )
        )

        files.add(
            ProjectFileEntity(
                projectId = project.id,
                relativePath = "app/proguard-rules.pro",
                content = "# Add project specific ProGuard rules here.\n-keepattributes SourceFile,LineNumberTable\n-keep public class * extends android.app.Activity"
            )
        )

        when (project.language) {
            "Kotlin" -> {
                when (project.template) {
                    "Compose Activity" -> {
                        files.add(
                            ProjectFileEntity(
                                projectId = project.id,
                                relativePath = "app/src/main/kotlin/$pkgPath/MainActivity.kt",
                                content = """
package ${project.packageName}

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("${project.name}")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    var count by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to ${'$'}name!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { count++ }) {
            Text("Clicked: ${'$'}count times")
        }
    }
}
                                """.trimIndent()
                            )
                        )
                    }
                    else -> {
                        files.add(
                            ProjectFileEntity(
                                projectId = project.id,
                                relativePath = "app/src/main/kotlin/$pkgPath/MainActivity.kt",
                                content = """
package ${project.packageName}

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btn_action)
        val text = findViewById<TextView>(R.id.tv_welcome)

        btn.setOnClickListener {
            counter++
            text.text = "Button tapped ${'$'}counter times in ${project.name}!"
            Toast.makeText(this, "DevCraft Mobile App Running!", Toast.LENGTH_SHORT).show()
        }
    }
}
                                """.trimIndent()
                            )
                        )
                        files.add(
                            ProjectFileEntity(
                                projectId = project.id,
                                relativePath = "app/src/main/res/layout/activity_main.xml",
                                content = """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="24dp"
    android:background="#121214">

    <TextView
        android:id="@+id/tv_welcome"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Welcome to ${project.name}"
        android:textColor="#FFFFFF"
        android:textSize="22sp"
        android:textStyle="bold" />

    <Button
        android:id="@+id/btn_action"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:text="Interact" />
</LinearLayout>
                                """.trimIndent()
                            )
                        )
                    }
                }
            }
            "Java" -> {
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "app/src/main/java/$pkgPath/MainActivity.java",
                        content = """
package ${project.packageName};

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.btn_action);
        TextView textView = findViewById(R.id.tv_welcome);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                textView.setText("Java Click Count: " + counter);
                Toast.makeText(MainActivity.this, "${project.name} Active", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
                        """.trimIndent()
                    )
                )
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "app/src/main/res/layout/activity_main.xml",
                        content = """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="24dp">

    <TextView
        android:id="@+id/tv_welcome"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="${project.name} (Java)"
        android:textSize="20sp" />

    <Button
        android:id="@+id/btn_action"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:text="Click Me" />
</LinearLayout>
                        """.trimIndent()
                    )
                )
            }
            "Python" -> {
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "main.py",
                        content = """
# Python Mobile App - ${project.name}
# Built with DevCraft Mobile Studio
import sys
import time

def main():
    print("=" * 40)
    print("🚀 Running ${project.name} (Python 3.11)")
    print("=" * 40)
    
    app_data = {
        "name": "${project.name}",
        "package": "${project.packageName}",
        "version": "${project.versionName}",
        "status": "Initialized successfully"
    }
    
    for key, value in app_data.items():
        print(f"[*] {key}: {value}")
        
    print("\nRunning application event loop...")
    for i in range(1, 4):
        time.sleep(0.5)
        print(f"[Cycle {i}] App responsive. Sensor telemetry: OK")
        
    print("\n✅ Python mobile execution completed cleanly!")

if __name__ == '__main__':
    main()
                        """.trimIndent()
                    )
                )
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "buildozer.spec",
                        content = """
[app]
title = ${project.name}
package.name = ${project.name.lowercase()}
package.domain = ${project.packageName.substringBeforeLast('.')}
source.dir = .
source.include_exts = py,png,jpg,kv,atlas,json
version = ${project.versionName}
requirements = python3,kivy,requests
orientation = portrait
osx.kivy_version = 2.2.0
fullscreen = 0
android.permissions = INTERNET,ACCESS_NETWORK_STATE
android.api = 34
android.minapi = 24
android.ndk = 25b
                        """.trimIndent()
                    )
                )
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "requirements.txt",
                        content = "kivy>=2.3.0\nrequests>=2.31.0\npillow>=10.2.0"
                    )
                )
            }
            "HTML" -> {
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "index.html",
                        content = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${project.name}</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="app-container">
        <header>
            <div class="logo-badge">⚡</div>
            <h1>${project.name}</h1>
            <p class="tagline">Interactive Web Application</p>
        </header>

        <main>
            <div class="card">
                <h3>Package: ${project.packageName}</h3>
                <p id="status-text">Ready for interaction</p>
                <div class="controls">
                    <button id="counter-btn" class="primary-btn">Tap to Count: <span id="count">0</span></button>
                    <button id="vibrate-btn" class="secondary-btn">Haptic Pulse</button>
                </div>
            </div>
            
            <div class="output-box" id="terminal-feed">
                > DevCraft Web Runtime v${project.versionName} initialized.
            </div>
        </main>
    </div>
    <script src="app.js"></script>
</body>
</html>
                        """.trimIndent()
                    )
                )
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "style.css",
                        content = """
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}
body {
    background-color: #121214;
    color: #F2F1F6;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    padding: 16px;
}
.app-container {
    width: 100%;
    max-width: 480px;
    background: #1E1D24;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.5);
    border: 1px solid #383545;
}
header {
    text-align: center;
    margin-bottom: 24px;
}
.logo-badge {
    font-size: 32px;
    margin-bottom: 8px;
}
h1 {
    font-size: 24px;
    color: #DCA683;
}
.tagline {
    font-size: 13px;
    color: #A19EAA;
}
.card {
    background: #27252F;
    border-radius: 12px;
    padding: 18px;
    margin-bottom: 16px;
}
.controls {
    display: flex;
    gap: 12px;
    margin-top: 16px;
}
.primary-btn {
    background: #DCA683;
    color: #28180E;
    border: none;
    padding: 12px 18px;
    border-radius: 8px;
    font-weight: bold;
    cursor: pointer;
    flex: 1;
}
.secondary-btn {
    background: #383545;
    color: #F2F1F6;
    border: none;
    padding: 12px 18px;
    border-radius: 8px;
    cursor: pointer;
}
.output-box {
    background: #0C0C0E;
    border-radius: 8px;
    padding: 12px;
    font-family: monospace;
    font-size: 12px;
    color: #4ADE80;
    min-height: 60px;
}
                        """.trimIndent()
                    )
                )
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "app.js",
                        content = """
let count = 0;
const countSpan = document.getElementById('count');
const counterBtn = document.getElementById('counter-btn');
const vibrateBtn = document.getElementById('vibrate-btn');
const terminalFeed = document.getElementById('terminal-feed');

counterBtn.addEventListener('click', () => {
    count++;
    countSpan.textContent = count;
    log(`Tapped count: ${'$'}{count}`);
});

vibrateBtn.addEventListener('click', () => {
    if (navigator.vibrate) {
        navigator.vibrate(200);
    }
    log("Haptic feedback triggered!");
});

function log(msg) {
    const time = new Date().toLocaleTimeString();
    terminalFeed.innerHTML += `<br>> [${'$'}{time}] ${'$'}{msg}`;
}
                        """.trimIndent()
                    )
                )
            }
            "C" -> {
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "app/src/main/cpp/native-lib.c",
                        content = """
#include <jni.h>
#include <string.h>
#include <stdio.h>

JNIEXPORT jstring JNICALL
Java_${project.packageName.replace('.', '_')}_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject thiz) {
    char message[128];
    snprintf(message, sizeof(message), "Hello from C Native Core in %s! (Optimized)", "${project.name}");
    return (*env)->NewStringUTF(env, message);
}

JNIEXPORT jint JNICALL
Java_${project.packageName.replace('.', '_')}_MainActivity_calculateSquare(
        JNIEnv* env,
        jobject thiz,
        jint val) {
    return val * val;
}
                        """.trimIndent()
                    )
                )
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "app/src/main/cpp/CMakeLists.txt",
                        content = """
cmake_minimum_required(VERSION 3.22.1)

project("${project.name}")

add_library(${project.name.lowercase()} SHARED
    native-lib.c
)

find_library(log-lib log)

target_link_libraries(${project.name.lowercase()}
    ${'$'}{log-lib}
)
                        """.trimIndent()
                    )
                )
                files.add(
                    ProjectFileEntity(
                        projectId = project.id,
                        relativePath = "app/src/main/kotlin/$pkgPath/MainActivity.kt",
                        content = """
package ${project.packageName}

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    external fun stringFromJNI(): String
    external fun calculateSquare(valNum: Int): Int

    companion object {
        init {
            System.loadLibrary("${project.name.lowercase()}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = stringFromJNI() + "\n5 squared = " + calculateSquare(5)
        setContentView(tv)
    }
}
                        """.trimIndent()
                    )
                )
            }
        }

        return files
    }

    fun generateAndroidManifest(project: ProjectEntity): String {
        val permissionsBuilder = StringBuilder()
        if (project.internetPermission) {
            permissionsBuilder.append("    <uses-permission android:name=\"android.permission.INTERNET\" />\n")
            permissionsBuilder.append("    <uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\" />\n")
        }
        if (project.notificationsPermission) {
            permissionsBuilder.append("    <uses-permission android:name=\"android.permission.POST_NOTIFICATIONS\" />\n")
        }
        if (project.filesPermission) {
            permissionsBuilder.append("    <uses-permission android:name=\"android.permission.READ_MEDIA_IMAGES\" />\n")
            permissionsBuilder.append("    <uses-permission android:name=\"android.permission.READ_MEDIA_VIDEO\" />\n")
        }
        if (project.locationPermission) {
            permissionsBuilder.append("    <uses-permission android:name=\"android.permission.ACCESS_FINE_LOCATION\" />\n")
            permissionsBuilder.append("    <uses-permission android:name=\"android.permission.ACCESS_COARSE_LOCATION\" />\n")
        }
        if (project.microphonePermission) {
            permissionsBuilder.append("    <uses-permission android:name=\"android.permission.RECORD_AUDIO\" />\n")
        }
        if (project.cameraPermission) {
            permissionsBuilder.append("    <uses-permission android:name=\"android.permission.CAMERA\" />\n")
        }

        return """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="${project.packageName}">

$permissionsBuilder
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="${project.name}"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Material3.DayNight.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
        """.trimIndent()
    }
}
