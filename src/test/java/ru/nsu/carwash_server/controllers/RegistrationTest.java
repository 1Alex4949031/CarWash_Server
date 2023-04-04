package ru.nsu.carwash_server.controllers;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.carwash_server.TestHelper;
import ru.nsu.carwash_server.models.RefreshToken;
import ru.nsu.carwash_server.models.Role;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.models.constants.ERole;
import ru.nsu.carwash_server.payload.request.LoginRequest;
import ru.nsu.carwash_server.payload.request.NewCarRequest;
import ru.nsu.carwash_server.payload.request.SignupRequest;
import ru.nsu.carwash_server.payload.request.TokenRefreshRequest;
import ru.nsu.carwash_server.repository.CarRepository;
import ru.nsu.carwash_server.repository.RoleRepository;
import ru.nsu.carwash_server.repository.UserRepository;
import ru.nsu.carwash_server.security.jwt.JwtUtils;
import ru.nsu.carwash_server.security.services.RefreshTokenService;
import ru.nsu.carwash_server.security.services.UserDetailsImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegistrationTest {

    private static final String API_AUTH_SIGNIN = "/api/auth/signin";
    private static final String API_AUTH_SIGNUP = "/api/auth/signup";
    private static final String API_AUTH_REFRESHTOKEN = "/api/auth/refreshtoken";
    private static final String API_USER_SAVENEWCAR = "/api/user/saveNewCar";
    private static final String API_AUTH_SIGNOUT = "/api/auth/signout";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String TEST_CAR_NUMBER = "A322GG";
    private static final String TEST_CAR_CLASS = "1";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CarRepository carRepository;

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
    public void testRegisterUser() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("user");
        SignupRequest request = SignupRequest.builder()
                .username("newuser")
                .role(roles)
                .password("newpassword")
                .build();

        mockMvc.perform(post(API_AUTH_SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully!")));

        assertTrue(userRepository.existsByUsername(request.getUsername()));
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        getUser();

        LoginRequest request = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post(API_AUTH_SIGNIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(not(emptyString()))))
                .andExpect(jsonPath("$.type", is("Bearer")))
                .andExpect(jsonPath("$.refreshToken", is(not(emptyString()))))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username", is(TEST_USERNAME)))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles", hasItem("ROLE_USER")))
                .andReturn();
        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");
        String type = JsonPath.read(result.getResponse().getContentAsString(), "$.type");
        var id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        String username = JsonPath.read(result.getResponse().getContentAsString(), "$.username");
        System.out.println(username + " " + id.toString() + " " + type);
    }

    @Test
    public void testRefreshToken() throws Exception {
        User user = getUser();

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        String token = jwtUtils.generateJwtToken(new UserDetailsImpl(user.getId(), user.getUsername(),
                user.getPassword(), authorities));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken(refreshToken.getToken());

        mockMvc.perform(post(API_AUTH_REFRESHTOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(TestHelper.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is(not(emptyString()))))
                .andExpect(jsonPath("$.refreshToken", is(not(emptyString()))));
    }

    @Test
    public void testLogoutUser() throws Exception {
        User user = getUser();

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        String token = jwtUtils.generateJwtToken(new UserDetailsImpl(user.getId(), user.getUsername(),
                user.getPassword(), authorities));
        refreshTokenService.createRefreshToken(user.getId());

        mockMvc.perform(post(API_AUTH_SIGNOUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Log out successful!")));
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
        Integer carId = JsonPath.read(resultOfSaveNewCar.getResponse().getContentAsString(), "$.carId");

        assertEquals(userId, idFromNewCar);
    }

    @Test
    public void bookOrder() throws Exception {
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
        Integer carId = JsonPath.read(resultOfSaveNewCar.getResponse().getContentAsString(), "$.carId");

        assertEquals(userId, idFromNewCar);
    }
}