package ru.nsu.carwash_server.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.carwash_server.TestHelper;
import ru.nsu.carwash_server.models.Role;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.models.constants.ERole;
import ru.nsu.carwash_server.payload.request.SignupRequest;
import ru.nsu.carwash_server.repository.RoleRepository;
import ru.nsu.carwash_server.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/sql/insert_roles.sql")
public class AuthRequestsTests {
    private static final String API_AUTH_SIGNIN = "/api/auth/signin";
    private static final String API_AUTH_SIGNUP = "/api/auth/signup";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        given(roleRepository.findByName(ERole.ROLE_USER)).willReturn(Optional.of(new Role(ERole.ROLE_USER)));
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(userRepository.findByUsername(any(String.class))).willReturn(Optional.empty());
    }

    @Test
    public void registrationTest() throws Exception {
        String username = "test_user";
        String password = "test_password";
        SignupRequest signupRequest = SignupRequest.builder()
                .username(username)
                .password(password)
                .build();

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());


        User savedUser = new User();
        savedUser.setUsername(signupRequest.getUsername());
        savedUser.setPassword(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        // Выполнение тестового запроса через mockMvc
        mockMvc.perform(post(API_AUTH_SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully!")));

        // Проверка, что объект User был успешно сохранен с указанным значением password и ролью
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        System.out.println("Capture user " + userCaptor.toString());
        User capturedUser = userCaptor.getValue();
        System.out.println("Capture user NEW NEW " + capturedUser.toString());
        assertEquals(capturedUser.getRoles().size(), 1);
        assertEquals(capturedUser.getRoles().iterator().next().getName(), ERole.ROLE_USER);
        assertEquals(capturedUser.getUsername(), signupRequest.getUsername());
        assertTrue(passwordEncoder.matches(signupRequest.getPassword(), capturedUser.getPassword()));
    }
}
