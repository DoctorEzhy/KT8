package com.example.dto.response

data class ErrorResponse(
    val message: String,
    val errors: Map<String, String> = emptyMap()
)