# Heroes Service (Второй веб-сервис, Вариант 67319)

2 веб-сервис управления командами героев на базе **JAX-RS** (Jakarta EE 10), работающий под управлением **Payara Server 7** 
---

## Порты и домены для запуска

Сервисы должны запускаться на двух **разных экземплярах Payara**:

| Сервис                    | Домен Payara | HTTPS порт | HTTP порт | Admin порт |
|:--------------------------| :--- | :--- | :--- | :--- |
| **Сервис 1 (HumanBeing)** | `domain1` | **`8181`** | *выключен* | `4848` |
| **Сервис 2 (Heroes)**     | `domain2` | **`9081`** | *выключен* | `9048` |

> Сервис 2, что Сервис 1 доступен по адресу:  
> `https://localhost:8181/human-beings` (Context Root: `/`).

## Настройка второго домена

### Создание второго домена со смещением портов
./bin/asadmin create-domain --portbase 9000 domain2

### Запуск второго домена
./bin/asadmin start-domain domain2

### Отключение HTTP (доступ строго по HTTPS)
./bin/asadmin --port 9048 set server-config.network-config.network-listeners.network-listener.http-listener-1.enabled=false

### Деплой сервиса
./bin/asadmin --port 9048 deploy --contextroot "/" target/heroes-service.war

## Реализованные эндпоинты
Базовый путь: https://localhost:9081/heroes

### DELETE /heroes/team/{team-id}/remove/{hero-id}
Удаляет героя из команды.

Пример проверки:

bash
```curl -k -i -X DELETE https://localhost:9081/heroes/team/7/remove/102``` 

### POST /heroes/team/{team-id}/car/add
Пересаживает всех героев команды без автомобиля на красные Lada Kalina (отправляет PUT-запросы в Сервис 1).

Пример проверки:

bash
```curl -k -i -X POST https://localhost:9081/heroes/team/7/car/add``` 



