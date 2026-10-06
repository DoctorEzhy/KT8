package com.example.dto

import com.example.dto.response.TaskResponse
import com.example.dto.response.UserResponse
import com.example.model.Task
import com.example.model.User

fun User.toResponse() = UserResponse(id = id, name = name, email = email)

fun Task.toResponse() = TaskResponse(
    id = id,
    userId = user.id,
    title = title,
    description = description,
    deadline = deadline,
    status = status,
    createdAt = createdAt
)