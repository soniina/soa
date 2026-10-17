package soa.humanbeings.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import soa.humanbeings.dto.HumanBeingSort
import soa.humanbeings.model.HumanBeingField

@Component
class HumanBeingSortConverter : Converter<String, HumanBeingSort> {
    override fun convert(source: String): HumanBeingSort {
        val parts = source.split(',')
        require(parts.size == 2) { "Ожидается сортировка в формате поле,asc|desc" }
        val field = HumanBeingField.entries.firstOrNull { it.property == parts[0] }
        requireNotNull(field) { "Неизвестное поле сортировки" }
        val direction = when (parts[1]) {
            "asc" -> Sort.Direction.ASC
            "desc" -> Sort.Direction.DESC
            else -> throw IllegalArgumentException("Направление должно быть asc или desc")
        }
        return HumanBeingSort(field, direction)
    }
}
