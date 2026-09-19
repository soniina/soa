package soa.humanbeings.dto

import org.springframework.data.domain.Sort
import soa.humanbeings.model.HumanBeingField

data class HumanBeingSort(
    val field: HumanBeingField,
    val direction: Sort.Direction
)
