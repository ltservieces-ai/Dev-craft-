package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TerminalSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TerminalDao {
    @Query("SELECT * FROM terminal_sessions WHERE projectId = :projectId ORDER BY lastActive DESC")
    fun getSessionsForProject(projectId: Long): Flow<List<TerminalSessionEntity>>

    @Query("SELECT * FROM terminal_sessions WHERE projectId = :projectId ORDER BY lastActive DESC LIMIT 1")
    suspend fun getLatestSession(projectId: Long): TerminalSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TerminalSessionEntity): Long

    @Update
    suspend fun updateSession(session: TerminalSessionEntity)

    @Query("DELETE FROM terminal_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)
}
