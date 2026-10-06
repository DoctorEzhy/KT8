package com.example.exception

import com.example.dto.response.ErrorResponse
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity

fun errorEntity(
    status: HttpStatusCode,
    message: String,
    errors: Map<String, String> = emptyMap()
): ResponseEntity<Any> = ResponseEntity.status(status).body(ErrorResponse(message, errors))