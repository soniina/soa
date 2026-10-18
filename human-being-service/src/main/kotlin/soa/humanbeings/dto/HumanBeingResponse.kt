package soa.humanbeings.dto

import java.time.LocalDateTime
import soa.humanbeings.model.HumanBeing
import soa.humanbeings.model.Mood
import soa.humanbeings.model.WeaponType

data class HumanBeingResponse(
    val id: Long,
    val name: String,
    val coordinates: CoordinatesResponse,
    val creationDate: LocalDateTime,
    val realHero: Boolean,
    val hasToothpick: Boolean,
    val impactSpeed: Float,
    val weaponType: WeaponType?,
    val mood: Mood,
    val car: CarResponse,
)

fun HumanBeing.toResponse(): HumanBeingResponse = HumanBeingResponse(
    id = id,
    name = name,
    coordinates = CoordinatesResponse(coordinates.x!!, coordinates.y!!),
    creationDate = creationDate,
    realHero = realHero,
    hasToothpick = hasToothpick,
    impactSpeed = impactSpeed,
    weaponType = weaponType,
    mood = mood,
    car = CarResponse(car.name!!, car.cool!!),
)
