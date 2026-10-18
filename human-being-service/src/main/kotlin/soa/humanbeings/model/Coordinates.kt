package soa.humanbeings.model

import jakarta.persistence.Embeddable
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull

@Embeddable
data class Coordinates(
    @field:NotNull
    @field:DecimalMax("740")
    @field:DecimalMin("-1.7976931348623157E308")
    var x: Double? = null,

    @field:NotNull
    @field:DecimalMax("913")
    @field:DecimalMin("-3.4028235E38")
    var y: Float? = null,
)
