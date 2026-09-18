package heroes.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import heroes.dto.HumanBeingDto;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@ApplicationScoped
public class FirstServiceClient {
    // URL первого сервиса
    private static final String BASE_URL = "https://localhost:8181/human-beings";
    private Client client;

    @PostConstruct
    public void init() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            this.client = ClientBuilder.newBuilder()
                    .sslContext(sslContext)
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось инициализировать HTTPS клиент", e);
        }
    }

    public HumanBeingDto getHeroById(Long id) {
        try {
            Response response = client.target(BASE_URL)
                    .path(String.valueOf(id))
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                return response.readEntity(HumanBeingDto.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public HumanBeingDto updateHero(Long id, HumanBeingDto hero) {
        Response response = client.target(BASE_URL)
                .path(String.valueOf(id))
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(hero, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 200) {
            return response.readEntity(HumanBeingDto.class);
        }
        throw new RuntimeException("Ошибка при обновлении героя: " + response.getStatus());
    }
}