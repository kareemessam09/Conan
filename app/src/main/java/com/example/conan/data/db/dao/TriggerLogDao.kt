package com.example.conan.data.db.dao

import androidx.room.*
import com.example.conan.data.models.TriggerLog
import com.example.conan.data.models.TriggerAction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TriggerLogDao {
    @Query("SELECT * FROM trigger_logs ORDER BY timestamp DESC")
    fun getAllTriggerLogs(): Flow<List<TriggerLog>>

    @Query("SELECT * FROM trigger_logs WHERE timestamp >= :fromDate ORDER BY timestamp DESC")
    fun getTriggerLogsFromDate(fromDate: LocalDateTime): Flow<List<TriggerLog>>

    @Query("SELECT COUNT(*) FROM trigger_logs WHERE userAction = :action AND timestamp >= :fromDate")
    suspend fun getActionCountSince(action: TriggerAction, fromDate: LocalDateTime): Int

    @Query("SELECT COUNT(*) FROM trigger_logs WHERE DATE(timestamp) = DATE(:date)")
    suspend fun getTriggerCountForDate(date: LocalDateTime): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTriggerLog(triggerLog: TriggerLog): Long

    @Delete
    suspend fun deleteTriggerLog(triggerLog: TriggerLog)

    @Query("DELETE FROM trigger_logs WHERE timestamp < :beforeDate")
    suspend fun deleteOldLogs(beforeDate: LocalDateTime)
}
