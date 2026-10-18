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

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;

@Service
public class FirstServiceClient {

    @Value("${service1.url:http://localhost:8765/human-beings}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public FirstServiceClient(RestTemplateBuilder builder) {
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
            System.err.println("Ошибка вызова первого сервиса: " + e.getMessage());
            return null;
        }
    }

    public HumanBeingDto updateHero(Long id, HumanBeingDto hero) {
        ResponseEntity<HumanBeingDto> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(hero),
                HumanBeingDto.class
        );
        return response.getBody();
    }
}