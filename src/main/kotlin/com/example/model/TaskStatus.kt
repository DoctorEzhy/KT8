package com.example.model

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE;

    fun canMoveTo(next: TaskStatus): Boolean = when (this) {
        TODO -> next == IN_PROGRESS || next == DONE
        IN_PROGRESS -> next == DONE
        DONE -> false
    }
}