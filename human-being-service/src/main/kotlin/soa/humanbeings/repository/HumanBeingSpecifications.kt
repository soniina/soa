package soa.humanbeings.repository

import jakarta.persistence.criteria.Path
import org.springframework.data.jpa.domain.Specification
import soa.humanbeings.dto.HumanBeingSearchRequest
import soa.humanbeings.model.HumanBeing

object HumanBeingSpecifications {
    fun matching(request: HumanBeingSearchRequest): Specification<HumanBeing> = Specification { root, _, cb ->
        val filters = mapOf(
            "id" to request.id,
            "name" to request.name,
            "coordinates.x" to request.coordinatesX,
            "coordinates.y" to request.coordinatesY,
            "creationDate" to request.creationDate,
            "realHero" to request.realHero,
            "hasToothpick" to request.hasToothpick,
            "impactSpeed" to request.impactSpeed,
            "weaponType" to request.weaponType,
            "mood" to request.mood,
            "car.name" to request.carName,
            "car.cool" to request.carCool,
        )

        val predicates = filters.filterValues { it != null }.map { (field, value) ->
            val path = field.split('.').fold(root as Path<*>) { path, part -> path.get<Any>(part) }
            cb.equal(path, value)
        }

        cb.and(*predicates.toTypedArray())
    }
}
