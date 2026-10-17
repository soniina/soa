package soa.humanbeings.dto

data class ErrorResponse(
    val message: String,
    val violations: List<FieldViolation> = emptyList()
)
