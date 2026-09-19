package soa.humanbeings.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.net.URI
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import soa.humanbeings.dto.HumanBeingRequest
import soa.humanbeings.dto.HumanBeingSearchRequest
import soa.humanbeings.dto.HumanBeingSort
import soa.humanbeings.model.HumanBeing
import soa.humanbeings.model.Mood
import soa.humanbeings.service.HumanBeingService

@RestController
@RequestMapping("/human-beings")
class HumanBeingController(
    private val service: HumanBeingService,
) {
    @GetMapping
    fun list(
        @Valid @ModelAttribute request: HumanBeingSearchRequest,
        @RequestParam(required = false) @Size(min = 1) sort: List<HumanBeingSort>?,
    ): ResponseEntity<List<HumanBeing>> {
        val orders = sort?.map { Sort.Order(it.direction, it.field.property) }
            ?: listOf(Sort.Order.asc("id"))
        val page = service.search(request, Sort.by(orders))
        return ResponseEntity.ok()
            .header("X-Total-Count", page.totalElements.toString())
            .body(page.content)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable @Positive id: Long): HumanBeing = service.get(id)

    @PostMapping(consumes = ["application/json"])
    fun create(
        @Valid @RequestBody body: HumanBeingRequest,
        request: HttpServletRequest
    ): ResponseEntity<HumanBeing> {
        val human = service.create(body)
        return ResponseEntity.created(URI.create("${request.contextPath}/human-beings/${human.id}"))
            .body(human)
    }

    @PutMapping("/{id}", consumes = ["application/json"])
    fun update(
        @PathVariable @Positive id: Long,
        @Valid @RequestBody body: HumanBeingRequest
    ): HumanBeing =
        service.update(id, body)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable @Positive id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/count/mood-less-than")
    fun count(@RequestParam mood: Mood): Map<String, Long> =
        mapOf("count" to service.countMoodLessThan(mood))

    @GetMapping("/search/name-contains")
    fun contains(@RequestParam @Size(min = 1) substring: String): List<HumanBeing> =
        service.findByNameContains(substring)

    @GetMapping("/search/name-prefix")
    fun prefix(@RequestParam @Size(min = 1) prefix: String): List<HumanBeing> =
        service.findByNamePrefix(prefix)
}
