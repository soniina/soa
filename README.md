# SOA

Два REST-сервиса и Vue-клиент для лабораторной работы по сервис-ориентированной архитектуре.

- `human-being-service/` — сервис персонажей на Kotlin, Spring Boot и Gradle.
- `heroes-service/` — сервис команд на Java, JAX-RS и Maven.
- `discovery-service/` — Eureka Server для регистрации экземпляров сервисов.
- `config-service/` — Spring Cloud Config Server с общими настройками сервисов.
- `api-gateway/` — Zuul Proxy с балансировкой Ribbon и поиском сервисов через Eureka.
- `frontend/` — общий клиент на Vue и Vite.

Спецификация API находится в [openapi.yaml](openapi.yaml), Swagger UI — в [index.html](index.html).
