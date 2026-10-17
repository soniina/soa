package soa.humanbeings.dto

import jakarta.validation.constraints.*
import java.time.LocalDateTime
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.BindParam
import soa.humanbeings.model.Mood
import soa.humanbeings.model.WeaponType

data class HumanBeingSearchRequest(
    @field:Min(1)
    val page: Long = 1,

    @field:Min(1)
    @field:Max(100)
    val size: Int = 20,

    @field:Min(1)
    val id: Long? = null,

    @field:Size(min = 1)
    val name: String? = null,

    @param:BindParam("coordinates.x")
    @field:DecimalMax("740")
    @field:DecimalMin("-1.7976931348623157E308")
    val coordinatesX: Double? = null,

    @param:BindParam("coordinates.y")
    @field:DecimalMax("913")
    @field:DecimalMin("-3.4028235E38")
    val coordinatesY: Float? = null,

    @param:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val creationDate: LocalDateTime? = null,

    val realHero: Boolean? = null,

    val hasToothpick: Boolean? = null,

    @field:DecimalMin(value = "-193", inclusive = false)
    @field:DecimalMax("3.4028235E38")
    val impactSpeed: Float? = null,

    val weaponType: WeaponType? = null,

    val mood: Mood? = null,

    @param:BindParam("car.name")
    @field:Size(min = 1)
    val carName: String? = null,

    @param:BindParam("car.cool")
    val carCool: Boolean? = null,
)
