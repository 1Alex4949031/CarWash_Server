# Тут будут все запросы
Надеюсь, что так будет проще анализировать всю информацию вокруг запросов, синхронизировать андроидроид и сервер лучше.  
Для примеров будут конкретные запросы со входными и выходными данными прямо как в Postam.  
## Запросы для регистрации и токена
1) ```@PostMapping("/api/auth/signup")``` - регистрация клиента  
На вход подается джейсон с информации о клиенте, а пример запроса:  
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
Или строка с сообщением об ошибке, что такое пользователь уже существует.  

2) ```@PostMapping("/api/auth/signin)``` - логин клиента  
На вход подаётся username (phone) и password  
```
{
    "username": "89635186660",
    "password": "testPassword"
}
```
Выходные данные - это информация о созданном bearer токене, имя пользователя и его роль
```
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4OTYzNTE4NjY2MCIsImlhdCI6MTY4MTgxNDk5NiwiZXhwIjoxNjgxODIwOTk2fQ.-tYEHbjGR_vWcfKcB-klbJ0EM4bdUTlyGJLeOIJ5ikqarCs15dpnkpKniAgU20GM8wc83Jfq6aE_OvykLjnxSQ",
    "type": "Bearer",
    "refreshToken": "62ef136e-65be-45e9-b3ac-534696211c80",
    "id": 2,
    "username": "89635186660",
    "roles": [
        "ROLE_USER"
    ]
}
```

3) ```@PostMapping("/api/auth/refreshtoken)``` - обновление токена  
На вход подаётся сам refresh token, время действия которого уже вышло    
```
{
    "refreshToken": "6f442bfe-be67-4af8-8d88-4ce20017f7c9"
}
```
   Выходные данные - мы получаем новый accessToken и всё тот же refreshToken: 
```
{
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4OTYzNTE4NjY2MCIsImlhdCI6MTY4MTgxNTg1NCwiZXhwIjoxNjgxODIxODU0fQ.vUj_6JTHyLmpx3VqwMwd5HU8QYlLO0_mnqArlZRAaNGig2PAURYLLkRtmHAbHSrCsbTLE--yHKQ0-h0-OhyJIA",
    "refreshToken": "6f442bfe-be67-4af8-8d88-4ce20017f7c9",
    "tokenType": "Bearer"
}
```

## Запросы пользователей для которых нужен access token в хедере  
Все запросы будут от лица пользователя, которому принадлежит этот токен
4) ```@PostMapping("api/user/saveNewCar")``` - пользователь добавляет новую машину  
На вход подаётся номер машины, тип кузова  
```
{
    "carNumber": "EN353T",
    "carClass" : "2 class"
}
 ```
На выход информация о машине, айды машины и пользователя, который её добавил  
```
{
    "carNumber": "EN353T",
    "carId": 1,
    "userId": 2,
    "carClass": "2 class"
}
```

5)```@PostMapping("api/orders/bookOrder")``` - бронирование заказа  
На вход подаётся вся информация о заказе и машина, для которой заказ    
```
{
    "administrator" : "Lesha22",
    "price":1.23,
    "startTime" : "2023-03-01T10:22:11.0+07",
    "endTime" : "2023-03-01T14:53:11.0+07",
    "name" : "moem car",
    "bonuses": 0,
    "specialist": "Misha22",
    "boxNumber":2,
    "autoId" : 1
}
```
На выход подаются все поля заказа, какими они сохранились, так как не вся информация обязательная в запросе:
```
{
    "id": 1,
    "price": 1.23,
    "name": "moem car",
    "startTime": "2023-03-01T03:22:11.000+00:00",
    "endTime": "2023-03-01T07:53:11.000+00:00",
    "administrator": "Lesha22",
    "specialist": "Misha22",
    "boxNumber": 2,
    "bonuses": 0,
    "booked": true,
    "executed": false,
    "comments": null,
    "userId": 2
}
```

6) ```@GetMapping("api/user/getUserCars")``` - просмотр своих машин  
На вход нужен только токен  
На выход сначала список машин пользователя, а потом информация о самом юзере  
```
{
    "autoList": [
        {
            "id": 1,
            "carNumber": "EN353T",
            "carClass": "2 class"
        },
        {
            "id": 2,
            "carNumber": "УП333T",
            "carClass": "1 class"
        }
    ],
    "user": {
        "id": 2,
        "username": "89635186660",
        "phone": "89635186660",
        "email": null,
        "bonuses": 100,
        "fullName": null
    }
}
```

7) ```@GetMapping("api/user/getUserOrders")``` - просмотр своих заказов  
На вход нужен только токен  
На выход сначала список заказов пользователя, а потом информация о самом юзере  
```
{
    "orders": [
        {
            "id": 1,
            "price": 1.23,
            "name": "moem car",
            "startTime": "2023-03-01T03:22:11.000+00:00",
            "endTime": "2023-03-01T07:53:11.000+00:00",
            "administrator": "Lesha22",
            "specialist": "Misha22",
            "boxNumber": 2,
            "bonuses": 0,
            "booked": true,
            "executed": false,
            "comments": null,
            "auto": {
                "id": 1,
                "carNumber": "EN353T",
                "carClass": "2 class"
            },
            "user": {
                "id": 2,
                "username": "89635186660",
                "phone": "89635186660",
                "email": null,
                "bonuses": 100,
                "fullName": null
            }
        },
        {
            "id": 2,
            "price": 1.23,
            "name": "moem car",
            "startTime": "2023-03-01T03:22:11.000+00:00",
            "endTime": "2023-03-01T07:53:11.000+00:00",
            "administrator": "Sasha",
            "specialist": "Andrei",
            "boxNumber": 2,
            "bonuses": 0,
            "booked": true,
            "executed": false,
            "comments": null,
            "auto": {
                "id": 2,
                "carNumber": "УП333T",
                "carClass": "1 class"
            },
            "user": {
                "id": 2,
                "username": "89635186660",
                "phone": "89635186660",
                "email": null,
                "bonuses": 100,
                "fullName": null
            }
        }
    ]
}
```

8) ```@PutMapping("api/user/updateUserInfo")``` - обновление/добавление какой-то информации о пользователи  
На вход вместе с токеном та информация, которую надо добавить/обновить (почту, телефон и ФИО)  
```
{
    "email" : "misha.23123123b32131ogdanov@gmail.com",
    "fullName": "Богданов Михаил Сергеевич213123123123edasd"
}
```
На выход сообщение о новой информации  
```
{
    "message": "Пользователь 2 получил почту misha.23123123b32131ogdanov@gmail.com и новый телефон null"
}
```

## Запросы для которых нужны права админа (токен админа)  
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
