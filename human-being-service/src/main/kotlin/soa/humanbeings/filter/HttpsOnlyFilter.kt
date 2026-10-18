package soa.humanbeings.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import soa.humanbeings.dto.ErrorResponse

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class HttpsOnlyFilter(private val mapper: ObjectMapper) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (!request.isSecure) {
            response.status = 403
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"
            mapper.writeValue(response.writer, ErrorResponse("Доступ разрешён только по HTTPS"))
            return
        }
        chain.doFilter(request, response)
    }
}
