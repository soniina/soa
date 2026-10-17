# Config Service

Spring Cloud Config Server на http://localhost:8888.
Настройки Human Beings хранятся в `config/human-being-service.yaml`.

Запуск из корня репозитория (Java 21):

```sh
./human-being-service/gradlew -p config-service bootRun
```

Сначала запустите Config Service и Discovery Service, затем Human Beings.
Для применения изменённых настроек перезапустите Human Beings.
