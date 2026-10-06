package com.example.service

import com.example.dto.response.DashboardResponse
import com.example.dto.toResponse
import com.example.model.TaskStatus
import com.example.model.User
import com.example.repository.TaskRepository
import com.example.repository.UserRepository
import com.example.service.result.UserResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UserService(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository
) {

    suspend fun createUser(name: String, email: String): UserResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (userRepository.existsByEmail(cleanEmail)) {
            UserResult.EmailTaken(cleanEmail)
        } else {
            UserResult.Success(userRepository.save(User(name = name.trim(), email = cleanEmail)))
        }
    }

    suspend fun getUser(id: Long): UserResult = withContext(Dispatchers.IO) {
        userRepository.findByIdOrNull(id)?.let { UserResult.Success(it) } ?: UserResult.NotFound(id)
    }

    suspend fun getDashboard(userId: Long): UserResult = coroutineScope {
        val userJob = async(Dispatchers.IO) { userRepository.findByIdOrNull(userId) }
        val totalJob = async(Dispatchers.IO) { taskRepository.countByUserId(userId) }
        val todoJob = async(Dispatchers.IO) {
            taskRepository.countByUserIdAndStatus(userId, TaskStatus.TODO)
        }
        val inProgressJob = async(Dispatchers.IO) {
            taskRepository.countByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS)
        }
        val doneJob = async(Dispatchers.IO) {
            taskRepository.countByUserIdAndStatus(userId, TaskStatus.DONE)
        }
        val overdueJob = async(Dispatchers.IO) {
            taskRepository.countByUserIdAndStatusNotAndDeadlineBefore(userId, TaskStatus.DONE, LocalDate.now())
        }
        val recentJob = async(Dispatchers.IO) { taskRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId) }

        val user = userJob.await()
        val total = totalJob.await()
        val todo = todoJob.await()
        val inProgress = inProgressJob.await()
        val done = doneJob.await()
        val overdue = overdueJob.await()
        val recent = recentJob.await()

        if (user == null) {
            UserResult.NotFound(userId)
        } else {
            UserResult.DashboardReady(
                DashboardResponse(
                    user = user.toResponse(),
                    totalTasks = total,
                    todoTasks = todo,
                    inProgressTasks = inProgress,
                    doneTasks = done,
                    overdueTasks = overdue,
                    recentTasks = recent.map { it.toResponse() }
                )
            )
        }
    }
}