package com.example.conan.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "trigger_logs")
data class TriggerLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appName: String,
    val triggerKeyword: String,
    val timestamp: LocalDateTime,
    val userAction: TriggerAction,
    val wasBlocked: Boolean = false
)

enum class TriggerAction {
    DISMISSED_WITH_OK,
    DISMISSED_COMPLETELY,
    REQUESTED_BLOCK,
    PANIC_BUTTON_USED
}
