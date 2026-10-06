package com.example.service

import com.example.dto.request.CreateTaskRequest
import com.example.dto.response.TaskResponse
import com.example.dto.toResponse
import com.example.model.Task
import com.example.model.TaskStatus
import com.example.repository.TaskRepository
import com.example.repository.UserRepository
import com.example.service.result.TaskResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

private const val STREAM_DELAY_MS = 500L

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) {

    suspend fun createTask(request: CreateTaskRequest): TaskResult = withContext(Dispatchers.IO) {
        val user = userRepository.findByIdOrNull(request.userId)
        if (user == null) {
            TaskResult.UserNotFound(request.userId)
        } else {
            val task = Task(
                title = request.title.trim(),
                description = request.description?.trim()?.takeIf { it.isNotEmpty() },
                deadline = request.deadline,
                user = user
            )
            TaskResult.Success(taskRepository.save(task))
        }
    }

    suspend fun getTask(id: Long): TaskResult = withContext(Dispatchers.IO) {
        taskRepository.findByIdOrNull(id)?.let { TaskResult.Success(it) } ?: TaskResult.TaskNotFound(id)
    }

    suspend fun startTask(id: Long): TaskResult = changeStatus(id, TaskStatus.IN_PROGRESS)

    suspend fun completeTask(id: Long): TaskResult = changeStatus(id, TaskStatus.DONE)

    private suspend fun changeStatus(id: Long, next: TaskStatus): TaskResult = withContext(Dispatchers.IO) {
        val task = taskRepository.findByIdOrNull(id) ?: return@withContext TaskResult.TaskNotFound(id)
        when {
            task.status == next -> TaskResult.AlreadyInStatus(id, next)
            !task.status.canMoveTo(next) -> TaskResult.InvalidTransition(id, task.status, next)
            else -> {
                task.status = next
                TaskResult.Success(taskRepository.save(task))
            }
        }
    }

    suspend fun deleteTask(id: Long): TaskResult = withContext(Dispatchers.IO) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id)
            TaskResult.Deleted(id)
        } else {
            TaskResult.TaskNotFound(id)
        }
    }

    fun streamTasks(userId: Long? = null): Flow<TaskResponse> = flow {
        val tasks = withContext(Dispatchers.IO) {
            if (userId == null) taskRepository.findAll() else taskRepository.findAllByUserId(userId)
        }
        for (task in tasks) {
            emit(task.toResponse())
            delay(STREAM_DELAY_MS)
        }
    }
}