package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val packageName: String,
    val location: String,
    val language: String, // "Kotlin", "Java", "Python", "HTML", "C"
    val minSdk: String,
    val useKts: Boolean = true,
    val template: String = "Empty Activity",
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val versionCode: Int = 1,
    val versionName: String = "1.0.0",
    val iconColorHex: String = "#38C779",
    val iconSymbol: String = "code",
    val gitRepoUrl: String = "",
    val notificationsPermission: Boolean = true,
    val filesPermission: Boolean = true,
    val locationPermission: Boolean = false,
    val microphonePermission: Boolean = false,
    val cameraPermission: Boolean = false,
    val internetPermission: Boolean = true
)
