# API Gateway

Zuul и Ribbon на Spring Boot 2.3 / Spring Cloud Hoxton. Нужна Java 11.
Сначала запустите Config Service, Discovery Service и экземпляры Human Beings.

Запуск из каталога `api-gateway`:

```sh
./gradlew bootRun
```

API доступен на https://localhost:8443/human-beings.
Настройки — в `config-service/config/api-gateway.yaml`.
Для HTTPS используется локальный сертификат Human Beings.
