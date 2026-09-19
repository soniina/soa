package soa.humanbeings.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import soa.humanbeings.dto.HumanBeingRequest
import soa.humanbeings.dto.HumanBeingSearchRequest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import soa.humanbeings.model.HumanBeing
import soa.humanbeings.model.Mood
import soa.humanbeings.repository.HumanBeingRepository
import soa.humanbeings.repository.HumanBeingSpecifications

@Service
@Transactional
class HumanBeingService(private val repository: HumanBeingRepository) {
    @Transactional(readOnly = true)
    fun get(id: Long): HumanBeing = repository.findById(id).orElseThrow {
        ResponseStatusException(HttpStatus.NOT_FOUND, "HumanBeing с идентификатором $id не найден")
    }

    fun create(request: HumanBeingRequest): HumanBeing =
        repository.save(applyRequest(HumanBeing(), request))

    fun update(id: Long, request: HumanBeingRequest): HumanBeing = applyRequest(get(id), request)

    fun delete(id: Long) = repository.delete(get(id))

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    fun search(request: HumanBeingSearchRequest, sort: Sort): Page<HumanBeing> {
        val specification = HumanBeingSpecifications.matching(request)

        if (request.page > Int.MAX_VALUE || (request.page - 1) * request.size > Int.MAX_VALUE) {
            return PageImpl(emptyList(), PageRequest.of(0, request.size), repository.count(specification))
        }

        val stableSort = if (sort.getOrderFor("id") == null) sort.and(Sort.by("id")) else sort
        return repository.findAll(
            specification,
            PageRequest.of((request.page - 1).toInt(), request.size, stableSort)
        )
    }

    @Transactional(readOnly = true)
    fun countMoodLessThan(mood: Mood): Long = repository.countByMoodIn(Mood.entries.take(mood.ordinal))

    @Transactional(readOnly = true)
    fun findByNameContains(substring: String): List<HumanBeing> =
        repository.findByNameContainingOrderByIdAsc(substring)

    @Transactional(readOnly = true)
    fun findByNamePrefix(prefix: String): List<HumanBeing> =
        repository.findByNameStartingWithOrderByIdAsc(prefix)

    private fun applyRequest(human: HumanBeing, request: HumanBeingRequest): HumanBeing = human.apply {
        name = request.name!!
        coordinates = request.coordinates!!
        realHero = request.realHero!!
        hasToothpick = request.hasToothpick!!
        impactSpeed = request.impactSpeed!!
        weaponType = request.weaponType
        mood = request.mood!!
        car = request.car!!
    }
}
