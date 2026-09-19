package soa.humanbeings.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.net.URI
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.bind.annotation.*
import soa.humanbeings.dto.HumanBeingRequest
import soa.humanbeings.dto.HumanBeingSearchRequest
import soa.humanbeings.dto.HumanBeingSort
import soa.humanbeings.dto.HumanBeingResponse
import soa.humanbeings.dto.toResponse
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
    ): ResponseEntity<List<HumanBeingResponse>> {
        val orders = sort?.map { Sort.Order(it.direction, it.field.property) }
            ?: listOf(Sort.Order.asc("id"))
        val page = service.search(request, Sort.by(orders))
        return ResponseEntity.ok()
            .header("X-Total-Count", page.totalElements.toString())
            .body(page.content.map { it.toResponse() })
    }

    @GetMapping("/{id}")
    fun get(@PathVariable @Positive id: Long): HumanBeingResponse =
        service.get(id)?.toResponse() ?: notFound(id)

    @PostMapping(consumes = ["application/json"])
    fun create(
        @Valid @RequestBody body: HumanBeingRequest,
        request: HttpServletRequest
    ): ResponseEntity<HumanBeingResponse> {
        val human = service.create(body)
        return ResponseEntity.created(URI.create("${request.contextPath}/human-beings/${human.id}"))
            .body(human.toResponse())
    }

    @PutMapping("/{id}", consumes = ["application/json"])
    fun update(
        @PathVariable @Positive id: Long,
        @Valid @RequestBody body: HumanBeingRequest
    ): HumanBeingResponse =
        service.update(id, body)?.toResponse() ?: notFound(id)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable @Positive id: Long): ResponseEntity<Void> {
        if (!service.delete(id)) notFound(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/count/mood-less-than")
    fun count(@RequestParam mood: Mood): Map<String, Long> =
        mapOf("count" to service.countMoodLessThan(mood))

    @GetMapping("/search/name-contains")
    fun contains(@RequestParam @Size(min = 1) substring: String): List<HumanBeingResponse> =
        service.findByNameContains(substring).map { it.toResponse() }

    @GetMapping("/search/name-prefix")
    fun prefix(@RequestParam @Size(min = 1) prefix: String): List<HumanBeingResponse> =
        service.findByNamePrefix(prefix).map { it.toResponse() }

    private fun notFound(id: Long): Nothing =
        throw ResponseStatusException(HttpStatus.NOT_FOUND, "HumanBeing с идентификатором $id не найден")
}
