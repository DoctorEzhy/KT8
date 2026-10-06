package com.example.controller

import com.example.dto.toResponse
import com.example.exception.errorEntity
import com.example.service.result.TaskResult
import com.example.service.result.UserResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun UserResult.toResponseEntity(successStatus: HttpStatus = HttpStatus.OK): ResponseEntity<Any> = when (this) {
    is UserResult.Success -> ResponseEntity.status(successStatus).body(user.toResponse())
    is UserResult.DashboardReady -> ResponseEntity.ok(dashboard)
    is UserResult.NotFound -> errorEntity(HttpStatus.NOT_FOUND, "Пользователь с id=$id не найден")
    is UserResult.EmailTaken -> errorEntity(HttpStatus.CONFLICT, "Пользователь с email $email уже существует")
}

fun TaskResult.toResponseEntity(successStatus: HttpStatus = HttpStatus.OK): ResponseEntity<Any> = when (this) {
    is TaskResult.Success -> ResponseEntity.status(successStatus).body(task.toResponse())
    is TaskResult.Deleted -> ResponseEntity.noContent().build()
    is TaskResult.TaskNotFound -> errorEntity(HttpStatus.NOT_FOUND, "Задача с id=$id не найдена")
    is TaskResult.UserNotFound -> errorEntity(HttpStatus.NOT_FOUND, "Пользователь с id=$userId не найден")
    is TaskResult.AlreadyInStatus -> errorEntity(HttpStatus.CONFLICT, "Задача с id=$id уже имеет статус $status")
    is TaskResult.InvalidTransition -> errorEntity(
        HttpStatus.CONFLICT,
        "Задачу с id=$id нельзя перевести из $fromStatus в $toStatus"
    )
}