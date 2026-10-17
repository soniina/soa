# Discovery Service

Eureka Server на Spring Boot 3.5 и Spring Cloud 2025.0. Работает локально на
http://localhost:8761, не регистрирует себя и не загружает реестр других серверов.

Запуск из корня репозитория (Java 21):

```sh
./human-being-service/gradlew -p discovery-service bootRun
```

Используется существующий Gradle Wrapper из `human-being-service`.
После запуска Human Beings в панели Eureka появится `HUMAN-BEING-SERVICE`.
Каждый экземпляр регистрируется отдельно, с указанием своего HTTPS-порта.
