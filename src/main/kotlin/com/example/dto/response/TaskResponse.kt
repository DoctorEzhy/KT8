package com.example.dto.response

import com.example.model.TaskStatus
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val deadline: LocalDate?,
    val status: TaskStatus,
    val createdAt: LocalDateTime
)