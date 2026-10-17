package soa.gateway;

import javax.net.ssl.SSLContext;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Ssl;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientFactory;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;

@SpringBootApplication
@EnableZuulProxy
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CloseableHttpClient httpClient(ApacheHttpClientFactory factory, ServerProperties serverProperties)
            throws Exception {
        Ssl ssl = serverProperties.getSsl();
        SSLContext context = SSLContexts.custom()
                .loadTrustMaterial(ResourceUtils.getURL(ssl.getKeyStore()), ssl.getKeyStorePassword().toCharArray())
                .build();
        return factory.createBuilder().setSSLContext(context).build();
    }
}
