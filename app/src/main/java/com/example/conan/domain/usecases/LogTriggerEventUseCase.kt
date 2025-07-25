package com.example.conan.domain.usecases

import com.example.conan.data.models.TriggerAction
import com.example.conan.domain.repository.TriggerLogRepository
import javax.inject.Inject

class LogTriggerEventUseCase @Inject constructor(
    private val triggerLogRepository: TriggerLogRepository
) {
    suspend operator fun invoke(
        appName: String,
        triggerKeyword: String,
        userAction: TriggerAction,
        wasBlocked: Boolean = false
    ): Long {
        return triggerLogRepository.logTriggerEvent(appName, triggerKeyword, userAction, wasBlocked)
    }
}
