# Human Being Service

Первый сервис лабораторной работы по СОА: Kotlin, Spring Boot 3.5 (Spring MVC),
Gradle, Java 21. Контракт обоих сервисов находится в `openapi.yaml`;
здесь реализован только API `/human-beings`.

## Локальный запуск

Нужны JDK 21 и PostgreSQL 14+. Gradle скачивается через wrapper.
Создайте отдельную базу и пользователя (команды запускаются от администратора PostgreSQL):

```sh
createuser --pwprompt soa
createdb --owner=soa soa
```

Настройте подключение перед запуском:

```sh
export DB_URL='jdbc:postgresql://localhost:5432/soa'
export DB_USERNAME='soa'
read -r -s DB_PASSWORD
export DB_PASSWORD
```

При первом запуске Flyway автоматически создаёт таблицу `human_beings` из
`src/main/resources/db/migration/V1__create_human_beings.sql`. Сервису нужна
отдельная база, пользователь должен иметь права на создание таблиц.
Пароль в файлы проекта не записывается.

```sh
./scripts/create-local-certificate.sh
./gradlew bootRun
```

Сертификат создаётся один раз в `certs/localhost.p12` для `localhost` и `127.0.0.1`.
Каталог `certs` не попадает в Git. Пароль локального сертификата по умолчанию —
`changeit`; можно задать `SSL_KEY_STORE_PASSWORD` при создании и запуске.
Другой keystore задаётся переменной `SSL_KEY_STORE` (например, `file:/path/server.p12`).

Сервис доступен по `https://localhost:8443`. Незашифрованный HTTP не обслуживается.
Самоподписанный сертификат нужно принять в клиенте; для локального curl используется `-k`.

```sh
curl -k -i https://localhost:8443/human-beings \
  -H 'Content-Type: application/json' \
  -d '{"name":"Alice","coordinates":{"x":120.5,"y":45.5},"realHero":true,"hasToothpick":false,"impactSpeed":120.5,"weaponType":"PISTOL","mood":"FRENZY","car":{"name":"Lada Kalina","cool":true}}'

curl -k -i 'https://localhost:8443/human-beings?page=1&size=20&mood=FRENZY&car.cool=true&sort=name,asc&sort=id,desc'
```

## API

| Метод | Путь | Операция |
|---|---|---|
| GET | `/human-beings` | Фильтрация, сортировка и страница коллекции |
| POST | `/human-beings` | Создание, ответ 201 и заголовок Location |
| GET | `/human-beings/{id}` | Получение объекта |
| PUT | `/human-beings/{id}` | Полная замена изменяемых полей |
| DELETE | `/human-beings/{id}` | Удаление, ответ 204 |
| GET | `/human-beings/count/mood-less-than?mood=APATHY` | Подсчёт по порядку LONGING < GLOOM < APATHY < FRENZY |
| GET | `/human-beings/search/name-contains?substring=lic` | Поиск подстроки в имени |
| GET | `/human-beings/search/name-prefix?prefix=Al` | Поиск префикса имени |

Фильтры по всем полям объединяются через AND и проверяют точное совпадение.
Поиск имён учитывает регистр. `sort` передаётся повторно для нескольких полей;
по умолчанию — `id,asc`. Для одинаковых значений используется порядок по id.
Сортировка строк, перечислений и null использует порядок PostgreSQL.
Страницы начинаются с 1, размер по умолчанию 20, максимум 100.
`X-Total-Count` содержит число объектов после фильтрации до разбиения на страницы.

Данные хранятся в PostgreSQL в таблице `human_beings` и сохраняются после
перезапуска. Id генерируется identity-колонкой, creationDate задаётся сервисом при создании объекта. PUT сохраняет оба поля. Фильтрация, сортировка, пагинация,
поиск и подсчёт выполняются в SQL. Количество записей и страница читаются
в одной транзакции REPEATABLE READ.

## Структура проекта

В `src/main/kotlin/soa/humanbeings` каждый класс и enum лежит в отдельном файле:

- `controller` — HTTP-методы и ответы API;
- `service` — операции с объектами, транзакции и проверка существования;
- `repository` — Spring Data JPA и Specification для фильтров;
- `model` — модель HumanBeing, вложенные объекты и перечисления;
- `dto` — тела запросов, параметры выборки и ошибки;
- `converter` — преобразование параметра сортировки в типизированную модель;
- `exception` — исключения и единый обработчик ошибок;
- `filter` — запрет HTTP.

JSON преобразуется Spring MVC в `HumanBeingRequest`; `@Valid` запускает Bean Validation.
Ошибки десериализации и валидации обрабатывает `ApiExceptionHandler`.

URL-параметры Spring связывает с `HumanBeingSearchRequest` через `@ModelAttribute`.
Параметры `sort=name,asc&sort=id,desc` Spring преобразует в список
`HumanBeingSort` через `HumanBeingSortConverter`. Конвертер проверяет поле
по перечислению `HumanBeingField` и направление `asc|desc`; ошибки дают 400. `PageRequest` переводит
номер страницы API (с 1) в номер страницы Spring Data (с 0).
`JpaRepository` предоставляет CRUD, `JpaSpecificationExecutor` применяет фильтры
через AND. Методы поиска по имени и подсчёта генерируются Spring Data.

Ошибки имеют поля `message` и `violations`: некорректный JSON или параметры —
400, нарушение модели — 422, отсутствующий объект — 404, неподдерживаемый
Content-Type — 415. Неизвестный URL возвращает HTML с кодом 404 по контракту.

## Сборка для Payara

```sh
./gradlew build
```

Результат — `build/libs/human-being-service.war`. WAR содержит
`SpringBootServletInitializer`; встроенный Tomcat помещён в `WEB-INF/lib-provided`
и не используется внешним сервером приложений. Этот же WAR можно запустить локально:

```sh
java -jar build/libs/human-being-service.war
```

Подготовлена конфигурация для Payara 6 (Jakarta EE 10, Java 21), context root `/`.
На Payara TLS настраивается на HTTPS listener самого сервера: параметры `server.ssl`
относятся только к встроенному серверу. Дополнительно фильтр приложения запрещает
незащищённые запросы с кодом 403. При развёртывании необходимо настроить сертификат
и отключить HTTP listener Payara. Payara пока не устанавливалась, запуск WAR в ней
и развёртывание на Helios ещё не проверялись.
