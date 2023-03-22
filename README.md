﻿# Spring Server for car wash application
### Общая информация
- Название базы данных: ```carWashDB```
- При регистрации пользователь отправляет username и password, затем телефоном автоматически ставится равный username (то есть в юзернейме пользователь передаёт свой телефон). А почту человек может потом отдельно написать, если захочет
- Есть три роли: админ, модератор, юзер. Можно легко добавить еще, пока что и запросов нет, в которых проверяются роли (только тестовые есть)
- Если при регистрации подаётся какая-то ерунда вместо роли или не подаётся ничего, то человек по автомату обычный юзер
- Парль в бд шифруется, никакие гады его не узнают! :+1:
- форматы даты : "yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ss.SSS", "EEE, dd MMM yyyy HH:mm:ss zzz", "yyyy-MM-dd" Если добавлять как в последнем примере, не указывая часы и минуты, то автоматически поставить время 07:00:00 (хз почему)
- ***Для определенных запросов пользователя (мы сами решаем каких), необходимо проверять, что запрос пользователя соответствует правам для этого запросами.\
С помощью специальных алгоритмов из данных о пользователи (имя, почта, пароль и тп) генерируется токен типа Bearer, а при дальнейших запросах мы проверяем в хедере мы передаём этот токен.\
Но токен не действует бесконечно, поэтому, если пользователь еще не вышел из приложения (то есть не просто свернул), то сервер должен отправлять такой запрос
То есть мы получаем новый accessToken и новый refreshToken для таких же махинаций в будующем.\
Думаю кратко этой информации хватит***

#### Запросы к обычным людям
1) ```@PostMapping("/api/auth/signup")``` - регистрация клиента\
На вход подается джейсон с информации о клиенте, а пример запроса - http://localhost:8080/api/auth/signup \
Входные данные : 
   ```
   {
    "username" : "896351866602",
    "password" : "12345678"
   }
   ``` 
   На выход просто получаем сообщение о том, что всё хорошо или ошибки\
   Для начала емаил подаётся как null, но может это исправить, если давать человеку возможность (как я понимаю флажок надо будет сделать просто)

2) ```@PostMapping("/api/auth/signin)``` - логин клиента\
На вход подаётся username (phone) и password\
   http://localhost:8080/api/auth/signin   
   ```
   {
    "username": "896351866602",
    "password": "12345678"
   }
   ```
   Тут выходные данные уже куда интереснее. Мы получаем информацию о том, кто залогинился (айди, телефон, почту, роли).\
   Еще мы получаем accessToken и refreshtoken. После 3 запроса об этом подробнее
3) ```@PostMapping("/api/auth/refreshtoken)``` - обновление токена\
На вход подаётся username (phone) и password\
   http://localhost:8080/api/auth/refreshtoken  
   ```
   {
    "refreshToken": "1ec7cabe-9932-4486-a5da-b8c199a16f5f"
   }
   ```
   Выходные данные: 
   ```
   {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3NzUwOTIwNSwiZXhwIjoxNjc3NTA5MjY1fQ.zuqA9WYYKrcBi4HcJxwkTE4y-m9q80dSReStF26v-sF3RMNpN3hqC9bRo5ki9DLen_dlrulfUFGOD66FgnaW2Q",
    "refreshToken": "1ec7cabe-9932-4486-a5da-b8c199a16f5f",
    "tokenType": "Bearer"
   }
   ```
4) ```@PostMapping("api/orders/newOrder")``` - добавление нового заказа\
На вход подаётся название услуги, дата, цена\
   http://localhost:8080/api/orders/newOrder 
   ```
   {
    "name": "моем машину",
    "date" : "2023-03-01T15:40:11.999",
    "price": 1.234
   }
   ```
На выход пока просто информация, что заказ добавлен
4) ```@PostMapping("api/orders//bookOrder")``` - бронирование существующего заказа\
На вход подаётся название услуги, дата, цена\
   http://localhost:8080/api/orders/newOrder 
   ```
   {
    "name": "моем машину",
    "date" : "2023-03-01T15:40:11.999",
    "price": 1.234
   }
   ```
На выход пока просто информация, что заказ добавлен
