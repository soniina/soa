package heroes.service;

import heroes.dto.HumanBeingDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class HumanBeingApiClient {

    @Value("${service1.url:http://localhost:8765/human-beings}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public HumanBeingApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    public HumanBeingDto getHeroById(Long id) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + id, HumanBeingDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            System.err.println("Ошибка обращения к Сервису 1 (через Gateway): " + e.getMessage());
            return null;
        }
    }

    public HumanBeingDto updateHero(Long id, HumanBeingDto hero) {
        try {
            ResponseEntity<HumanBeingDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    new HttpEntity<>(hero),
                    HumanBeingDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении героя: " + e.getMessage());
            return null;
        }
    }
}