# Тут будут все запросы
Надеюсь, что так будет проще анализировать всю информацию вокруг запросов, синхронизировать андроидроид и сервер лучше.  
Для примеров будут конкретные запросы со входными и выходными данными прямо как в Postam.  
## Запросы к обычным людям
1) ```@PostMapping("/api/auth/signup")``` - регистрация клиента\
На вход подается джейсон с информации о клиенте, а пример запроса:\
Входные данные : 
```
{
    "username" : "89635186660",
    "password" : "testPassword"
}
``` 
Выходные данные:
```
{
    "message": "User registered successfully!"
}
```
Или строка с сообщением об ошибке, что такое пользователь уже существует.\ 
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
На вход подаётся сам refresh token, время действия которого уже вышло\
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
5) ```@PostMapping("api/orders//bookOrder")``` - бронирование существующего заказа\
На вход подаётся название услуги, дата, цена\
   http://localhost:8080/api/orders/bookOrder 
   ```
   {
    "name": "моем машину",
    "date" : "2023-03-01T15:40:11.999",
    "price": 1.234
   }
   ```
