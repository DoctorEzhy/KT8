package com.example.service.result

import com.example.dto.response.DashboardResponse
import com.example.model.User

sealed class UserResult {
    data class Success(val user: User) : UserResult()
    data class DashboardReady(val dashboard: DashboardResponse) : UserResult()
    data class NotFound(val id: Long) : UserResult()
    data class EmailTaken(val email: String) : UserResult()
}