# UPD 15.04.2023
Я уговорил своего друга с 4 курса прийти ко мне и помочь разобраться с проектом, то есть тестами. Вот основное что я узнал:
- Я постараюсь к завтрашнему вечеру написать примеры тестов по этой новой системе, надеюсь, все будет проще и я разберусь
- Тестирование с помощью моков херня (я не до конца понял почему)
- Надо использовать так называемые testContainers, которые создают в Docker свою бд для каждого теста и ты тестируешься как будто в мэине на реальном сервере.
- Довольно долго он мне всё это настраивал, потому что была проблема с подключением миграций туда, но, вроде, сейчас оно подключает бд и работает, но мне надо переделать все тесты
- Если ты когда-то пользовался этими тест-контейнерами или слышала что-то про них, то можешь дать небольшой фидбек. Я так-то очень верю этому другу, он в цфт как раз в основном на спринге чем-то занимается и вообще парень он серьёзный крутой, но ты ведь явно покруче будешь и, если ты скажешь что это плохая штука, то я постараюсь найти другой способ 
- *Сам этот тест собственно выглядит вот [так](https://github.com/Allody22/CarWash_Server/blob/master/src/test/java/ru/nsu/carwash_server/controllers/RegistrationControllerTest.java)*

## Я сижу разбираю с этими тестами дальше и вот что я могу сказать (может быть информация и неправильная, но это всё что я нагуглил)
Надо делить тесты на тесты для базы данных и тесты для запросов. Я это можно сказать миксовал, поэтому наверное так и получалось.
В тестах для баз данных репозитории идут с аннотацией @Autowired (как было у меня) и там можно тупо обращаться к репозиторию сразу.
В тестах для контроллеров репозитории идут с аннотацией @MockBean, а методы обращения к репозиториям там надо глушить и это какой-то кошмар.
### ТЕСТИРОВАНИЕ ЗАПРОСОВ С АННОТАЦИЕЙ @MockBean
Получается вот такой цирк.
*У меня есть контроллер для регистрации в котором я делаю метод save для юзер репозитория, а ещё в этом контроллере я делаю save для репозитория ролей => я должен заглушить (вообще кажется что это почти то же самое что переопределить) эти методы для каждого репозитория и для каждого одного юзера и для каждой одной роли для которой я это делаю
То есть я писал эти запросы, которые всегда будут работать для любого класса User, чтобы в тестах мне пришлось глушить все методы в репозиториях для каждого созданного юзера*
Сейчас будет код того, как примерно делаются заглушки
```
// Настройка заглушки для PasswordEncoder
String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

// Настройка заглушки для UserRepository
User savedUser = new User();
savedUser.setUsername(signupRequest.getUsername());
savedUser.setPassword(encodedPassword);
Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

// Настройка заглушки для RoleRepository
Role userRole = new Role(ERole.ROLE_USER);
Mockito.when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
```
И это все только для регистрации пользователя. А я ведь для любых запросов дальше я должен регистрировать пользователя, затем логиниться им, добавлять машину и так далее.
Получается ужасно много заглушек (моков), которые надо писать каждый раз.
Вот пример (надеюсь что правильный) [моего теста](https://github.com/Allody22/CarWash_Server/blob/master/src/test/java/ru/nsu/carwash_server/controllers/RegisterRequestsTest.java) с заглушками для запроса регистрации, мне не хватило сил опять делать заглушки для всего для любого другого запроса
