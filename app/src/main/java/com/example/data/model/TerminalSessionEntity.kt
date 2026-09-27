package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "terminal_sessions")
data class TerminalSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val sessionName: String = "Session 1",
    val logs: String = "",
    val lastActive: Long = System.currentTimeMillis()
)
