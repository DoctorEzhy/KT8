package com.example.exception

import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidation(ex: WebExchangeBindException): ResponseEntity<Any> {
        val errors = ex.bindingResult.fieldErrors
            .groupBy({ it.field }, { it.defaultMessage ?: "Некорректное значение" })
            .mapValues { (_, messages) -> messages.joinToString("; ") }
        return errorEntity(HttpStatus.BAD_REQUEST, "Ошибка валидации", errors)
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleBadInput(ex: ServerWebInputException): ResponseEntity<Any> =
        errorEntity(HttpStatus.BAD_REQUEST, "Некорректный запрос: проверьте формат данных (JSON, числа в адресе)")

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleConflict(ex: DataIntegrityViolationException): ResponseEntity<Any> =
        errorEntity(HttpStatus.CONFLICT, "Данные нарушают ограничения базы (например, email уже занят)")

    @ExceptionHandler(ResponseStatusException::class)
    fun handleStatus(ex: ResponseStatusException): ResponseEntity<Any> =
        errorEntity(ex.statusCode, ex.reason ?: "Ошибка запроса")

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<Any> {
        log.error("Необработанная ошибка", ex)
        return errorEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера")
    }
}