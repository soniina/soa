package soa.humanbeings.exception

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import soa.humanbeings.dto.ErrorResponse
import soa.humanbeings.dto.FieldViolation

@RestControllerAdvice
class ApiExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ResponseStatusException::class)
    fun responseStatus(exception: ResponseStatusException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(exception.statusCode)
            .body(ErrorResponse(exception.reason ?: "Ошибка запроса"))

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun unreadable(exception: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val cause = exception.cause

        if (
            cause is JsonMappingException &&
            exception.mostSpecificCause !is JsonParseException &&
            !cause.originalMessage.startsWith("Trailing token")
        ) {
            val field = cause.path.joinToString(".") { it.fieldName ?: "[${it.index}]" }
            return ResponseEntity.unprocessableEntity().body(
                ErrorResponse(
                    "Объект не прошёл проверку полей",
                    listOf(FieldViolation(field.ifEmpty { "body" }, "Некорректный тип или значение поля")),
                )
            )
        }
        return ResponseEntity.badRequest()
            .body(ErrorResponse("Тело запроса отсутствует или содержит некорректный JSON"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validation(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(
            if (exception.parameter.hasParameterAnnotation(org.springframework.web.bind.annotation.RequestBody::class.java))
                422
            else
                400
        ).body(
            ErrorResponse(
                "Переданные данные не прошли проверку",
                exception.bindingResult.fieldErrors
                    .map {
                        FieldViolation(it.field, it.defaultMessage ?: "Некорректное значение поля")
                    }
                    .sortedBy { it.field },
            )
        )

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException::class)
    fun parameter(
        exception: org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(
            ErrorResponse(
                "Некорректный параметр запроса",
                listOf(FieldViolation(exception.name, "Некорректный тип или значение"))
            )
        )

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun mediaType(): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(415)
            .body(ErrorResponse("Поддерживается только Content-Type application/json"))

    @ExceptionHandler(NoHandlerFoundException::class, NoResourceFoundException::class)
    fun notFound(): ResponseEntity<String> =
        ResponseEntity.status(404)
            .contentType(MediaType.TEXT_HTML)
            .body("<!doctype html><html><head><title>404 Not Found</title></head><body><h1>Not Found</h1></body></html>")

    @ExceptionHandler(Exception::class)
    fun unexpected(exception: Exception): ResponseEntity<ErrorResponse> {
        if (exception is org.springframework.web.ErrorResponse) {
            return ResponseEntity.status(exception.statusCode)
                .body(ErrorResponse(exception.body.detail ?: "Ошибка запроса"))
        }

        logger.error("Ошибка обработки запроса", exception)
        return ResponseEntity.internalServerError().body(ErrorResponse("Произошла внутренняя ошибка сервиса"))
    }
}
