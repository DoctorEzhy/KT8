package com.example.repository

import com.example.model.Task
import com.example.model.TaskStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface TaskRepository : JpaRepository<Task, Long> {
    fun findAllByUserId(userId: Long): List<Task>

    fun findTop5ByUserIdOrderByCreatedAtDesc(userId: Long): List<Task>

    fun countByUserId(userId: Long): Long

    fun countByUserIdAndStatus(userId: Long, status: TaskStatus): Long

    fun countByUserIdAndStatusNotAndDeadlineBefore(userId: Long, status: TaskStatus, date: LocalDate): Long
}