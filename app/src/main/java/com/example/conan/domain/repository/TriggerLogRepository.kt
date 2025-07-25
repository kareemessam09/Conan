package com.example.conan.domain.repository

import com.example.conan.data.models.TriggerLog
import com.example.conan.data.models.TriggerAction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface TriggerLogRepository {
    fun getAllTriggerLogs(): Flow<List<TriggerLog>>
    fun getTriggerLogsFromDate(fromDate: LocalDateTime): Flow<List<TriggerLog>>
    suspend fun getActionCountSince(action: TriggerAction, fromDate: LocalDateTime): Int
    suspend fun getTriggerCountForDate(date: LocalDateTime): Int
    suspend fun logTriggerEvent(appName: String, triggerKeyword: String, userAction: TriggerAction, wasBlocked: Boolean = false): Long
    suspend fun deleteOldLogs(beforeDate: LocalDateTime)
}
