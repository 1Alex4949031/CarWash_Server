package ru.nsu.carwash_server.controllers;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.carwash_server.TestHelper;
import ru.nsu.carwash_server.models.Auto;
import ru.nsu.carwash_server.models.Role;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.models.constants.ERole;
import ru.nsu.carwash_server.payload.request.BookingOrderRequest;
import ru.nsu.carwash_server.payload.request.LoginRequest;
import ru.nsu.carwash_server.payload.request.NewCarRequest;
import ru.nsu.carwash_server.payload.request.UpdateUserInfoRequest;
import ru.nsu.carwash_server.repository.CarRepository;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.RoleRepository;
import ru.nsu.carwash_server.repository.UserRepository;
import ru.nsu.carwash_server.security.jwt.JwtUtils;
import ru.nsu.carwash_server.security.services.RefreshTokenService;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserTest {

    private static final String API_AUTH_SIGNIN = "/api/auth/signin";
    private static final String API_AUTH_SIGNUP = "/api/auth/signup";
    private static final String API_AUTH_REFRESHTOKEN = "/api/auth/refreshtoken";

    private static final String API_AUTH_CHANGEUSERINFO = "/api/user/updateUserInfo";
    private static final String API_ORDERS_BOOKORDER = "/api/orders/bookOrder";
    private static final String API_USER_SAVENEWCAR = "/api/user/saveNewCar";
    private static final String API_AUTH_SIGNOUT = "/api/auth/signout";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String TEST_CAR_NUMBER = "A322GG";
    private static final String TEST_CAR_CLASS = "1";


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;


    private User getUser() {
        Role role = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = new User(TEST_USERNAME, encoder.encode(TEST_PASSWORD));
        user.setRoles(roles);
        userRepository.save(user);
        return user;
    }

    @Test
    public void bookNewOrder() throws Exception {
        User user = getUser();

        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        MvcResult resultOfLogin = mockMvc.perform(post(API_AUTH_SIGNIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(not(emptyString()))))
                .andExpect(jsonPath("$.type", is("Bearer")))
                .andExpect(jsonPath("$.refreshToken", is(not(emptyString()))))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username", is(TEST_USERNAME)))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles", hasItem("ROLE_USER")))
                .andReturn();

        String token = JsonPath.read(resultOfLogin.getResponse().getContentAsString(), "$.token");
        Integer userId = JsonPath.read(resultOfLogin.getResponse().getContentAsString(), "$.id");


        NewCarRequest newCarRequest = new NewCarRequest(TEST_CAR_NUMBER, TEST_CAR_CLASS);
        MvcResult resultOfSaveNewCar = mockMvc.perform(post(API_USER_SAVENEWCAR)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(newCarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carNumber", is(TEST_CAR_NUMBER)))
                .andExpect(jsonPath("$.carClass", is(TEST_CAR_CLASS)))
                .andExpect(jsonPath("$.carId").isNumber())
                .andExpect(jsonPath("$.userId").isNumber())
                .andReturn();

        Integer userIdFromNewCar = JsonPath.read(resultOfSaveNewCar.getResponse().getContentAsString(), "$.userId");
        Integer newCarIdInt = JsonPath.read(resultOfSaveNewCar.getResponse().getContentAsString(), "$.carId");
        assertEquals(userId, userIdFromNewCar);
        Long newCarId = Long.valueOf(newCarIdInt);


        Date currentTime = new Date();
        Date currentTimePlusFiveH = new Date();
        long timeInMilli = currentTimePlusFiveH.getTime();
        long fiveHours = 5 * 60 * 60 * 1000;
        currentTimePlusFiveH.setTime(timeInMilli + fiveHours);

        BookingOrderRequest bookingOrderRequest = BookingOrderRequest.builder()
                .price(3.123)
                .name("Car washing")
                .startTime(currentTime)
                .administrator("Misha")
                .endTime(currentTimePlusFiveH)
                .autoId(newCarId)
                .build();

        mockMvc.perform(post(API_ORDERS_BOOKORDER)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(bookingOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.price", is(3.123)))
                .andExpect(jsonPath("$.administrator",is("Misha")))
                .andExpect(jsonPath("$.startTime").isNotEmpty());

        User userNewUsername = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));

        System.out.println("Same user");
        System.out.println(userNewUsername);

        UpdateUserInfoRequest updateUserInfoRequest = UpdateUserInfoRequest.builder()
                .fullName("Misha b")
                .username("4325678")
                .build();

        ResultActions resultActions = mockMvc.perform(put(API_AUTH_CHANGEUSERINFO)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(updateUserInfoRequest)))
                .andExpect(status().isOk());

        //userRepository.changeUserInfo(null, "333666",userNewUsername.getId(),null);
        User userNewUsernameFromRepository = userRepository.findById(userNewUsername.getId())
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));
        System.out.println(userNewUsernameFromRepository);
        // ПОЧЕМУ autoSet РАВЕН НУЛЮ
        Set<Auto> autoSet = userNewUsernameFromRepository.getAuto();
        assertEquals(1, autoSet.stream().filter(it -> it.getId().equals(newCarId)).count());
    }

    @Test
    public void saveNewCar() throws Exception {
        getUser();

        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        MvcResult resultOfLogin = mockMvc.perform(post(API_AUTH_SIGNIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(not(emptyString()))))
                .andExpect(jsonPath("$.type", is("Bearer")))
                .andExpect(jsonPath("$.refreshToken", is(not(emptyString()))))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username", is(TEST_USERNAME)))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles", hasItem("ROLE_USER")))
                .andReturn();

        String token = JsonPath.read(resultOfLogin.getResponse().getContentAsString(), "$.token");
        Integer userId = JsonPath.read(resultOfLogin.getResponse().getContentAsString(), "$.id");

        NewCarRequest newCarRequest = new NewCarRequest(TEST_CAR_NUMBER, TEST_CAR_CLASS);


        MvcResult resultOfSaveNewCar = mockMvc.perform(post(API_USER_SAVENEWCAR)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(newCarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carNumber", is(TEST_CAR_NUMBER)))
                .andExpect(jsonPath("$.carClass", is(TEST_CAR_CLASS)))
                .andExpect(jsonPath("$.carId").isNumber())
                .andExpect(jsonPath("$.userId").isNumber())
                .andReturn();

        Integer idFromNewCar = JsonPath.read(resultOfSaveNewCar.getResponse().getContentAsString(), "$.userId");

        assertEquals(userId, idFromNewCar);
    }
}