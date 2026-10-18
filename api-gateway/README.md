# API Gateway

Zuul и Ribbon на Spring Boot 2.3 / Spring Cloud Hoxton. Нужна Java 11.
Сначала запустите Config Service, Discovery Service и экземпляры Human Beings.

Запуск из каталога `api-gateway`:

```sh
./gradlew bootRun
```

API: https://helios.cs.ifmo.ru:28082/human-beings.
Настройки — в `config-service/config/api-gateway.yaml`.
Сертификат gateway — `certs/gateway.p12`, доверие к Human Beings — `certs/truststore.p12`.
