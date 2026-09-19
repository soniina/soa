package soa.humanbeings.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import soa.humanbeings.model.Car
import soa.humanbeings.model.Coordinates
import soa.humanbeings.model.Mood
import soa.humanbeings.model.WeaponType

data class HumanBeingRequest(
    @field:NotNull
    @field:Size(min = 1)
    val name: String? = null,

    @field:NotNull
    @field:Valid
    val coordinates: Coordinates? = null,

    @field:NotNull
    val realHero: Boolean? = null,

    @field:NotNull
    val hasToothpick: Boolean? = null,

    @field:NotNull
    @field:DecimalMin(value = "-193", inclusive = false)
    @field:DecimalMax("3.4028235E38")
    val impactSpeed: Float? = null,

    val weaponType: WeaponType? = null,

    @field:NotNull
    val mood: Mood? = null,

    @field:NotNull
    @field:Valid
    val car: Car? = null,
)
