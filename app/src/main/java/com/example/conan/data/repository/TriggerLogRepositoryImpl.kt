package com.example.conan.data.repository

import com.example.conan.data.db.dao.TriggerLogDao
import com.example.conan.data.models.TriggerLog
import com.example.conan.data.models.TriggerAction
import com.example.conan.domain.repository.TriggerLogRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TriggerLogRepositoryImpl @Inject constructor(
    private val triggerLogDao: TriggerLogDao
) : TriggerLogRepository {

    override fun getAllTriggerLogs(): Flow<List<TriggerLog>> = triggerLogDao.getAllTriggerLogs()

    override fun getTriggerLogsFromDate(fromDate: LocalDateTime): Flow<List<TriggerLog>> =
        triggerLogDao.getTriggerLogsFromDate(fromDate)

    override suspend fun getActionCountSince(action: TriggerAction, fromDate: LocalDateTime): Int =
        triggerLogDao.getActionCountSince(action, fromDate)

    override suspend fun getTriggerCountForDate(date: LocalDateTime): Int =
        triggerLogDao.getTriggerCountForDate(date)

    override suspend fun logTriggerEvent(
        appName: String,
        triggerKeyword: String,
        userAction: TriggerAction,
        wasBlocked: Boolean
    ): Long {
        val triggerLog = TriggerLog(
            appName = appName,
            triggerKeyword = triggerKeyword,
            timestamp = LocalDateTime.now(),
            userAction = userAction,
            wasBlocked = wasBlocked
        )
        return triggerLogDao.insertTriggerLog(triggerLog)
    }

    override suspend fun deleteOldLogs(beforeDate: LocalDateTime) =
        triggerLogDao.deleteOldLogs(beforeDate)
}
