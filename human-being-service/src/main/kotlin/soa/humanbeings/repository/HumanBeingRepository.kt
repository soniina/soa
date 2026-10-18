package soa.humanbeings.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import soa.humanbeings.model.HumanBeing
import soa.humanbeings.model.Mood

interface HumanBeingRepository : JpaRepository<HumanBeing, Long>, JpaSpecificationExecutor<HumanBeing> {
    fun findByNameContainingOrderByIdAsc(substring: String): List<HumanBeing>
    fun findByNameStartingWithOrderByIdAsc(prefix: String): List<HumanBeing>
    fun countByMoodIn(moods: Collection<Mood>): Long
}
