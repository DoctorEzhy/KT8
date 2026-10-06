package com.example.controller

import com.example.dto.request.CreateTaskRequest
import com.example.dto.response.TaskResponse
import com.example.service.TaskService
import com.example.service.UserService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService,
    private val userService: UserService
) {

    @PostMapping
    suspend fun createTask(@Valid @RequestBody request: CreateTaskRequest): ResponseEntity<Any> =
        taskService.createTask(request).toResponseEntity(HttpStatus.CREATED)

    @GetMapping("/{id}")
    suspend fun getTask(@PathVariable id: Long): ResponseEntity<Any> =
        taskService.getTask(id).toResponseEntity()

    // TODO -> IN_PROGRESS
    @PatchMapping("/{id}/start")
    suspend fun startTask(@PathVariable id: Long): ResponseEntity<Any> =
        taskService.startTask(id).toResponseEntity()

    // TODO или IN_PROGRESS -> DONE
    @PatchMapping("/{id}/complete")
    suspend fun completeTask(@PathVariable id: Long): ResponseEntity<Any> =
        taskService.completeTask(id).toResponseEntity()

    @DeleteMapping("/{id}")
    suspend fun deleteTask(@PathVariable id: Long): ResponseEntity<Any> =
        taskService.deleteTask(id).toResponseEntity()

    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamAll(): Flow<TaskResponse> = taskService.streamTasks()

    @GetMapping("/user/{userId}/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamUserTasks(@PathVariable userId: Long): Flow<TaskResponse> = taskService.streamTasks(userId)

    @GetMapping("/user/{userId}/dashboard")
    suspend fun getUserDashboard(@PathVariable userId: Long): ResponseEntity<Any> =
        userService.getDashboard(userId).toResponseEntity()
}