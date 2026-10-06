package com.example.service.result

import com.example.model.Task
import com.example.model.TaskStatus

sealed class TaskResult {
    data class Success(val task: Task) : TaskResult()
    data class Deleted(val id: Long) : TaskResult()
    data class TaskNotFound(val id: Long) : TaskResult()
    data class UserNotFound(val userId: Long) : TaskResult()
    data class AlreadyInStatus(val id: Long, val status: TaskStatus) : TaskResult()
    data class InvalidTransition(val id: Long, val fromStatus: TaskStatus, val toStatus: TaskStatus) : TaskResult()
}