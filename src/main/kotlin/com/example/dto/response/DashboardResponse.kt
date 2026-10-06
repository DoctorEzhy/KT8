package com.example.dto.response

data class DashboardResponse(
    val user: UserResponse,
    val totalTasks: Long,
    val todoTasks: Long,
    val inProgressTasks: Long,
    val doneTasks: Long,
    val overdueTasks: Long,
    val recentTasks: List<TaskResponse>
)