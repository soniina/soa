package soa.humanbeings.model

import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Embeddable
data class Car(
    @field:NotNull
    @field:Size(min = 1)
    var name: String? = null,

    @field:NotNull
    var cool: Boolean? = null,
)
