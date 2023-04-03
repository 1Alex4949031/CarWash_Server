package ru.nsu.carwash_server.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.carwash_server.models.constants.ERole;

import java.util.HashSet;
import java.util.Set;

public class UserTest {

    @Test
    public void testConstructorWithUsernameAndPassword() {
        String username = "testUser";
        String password = "testPassword";
        User user = new User(username, password);
        Assertions.assertEquals(username, user.getUsername());
        Assertions.assertEquals(password, user.getPassword());
        Assertions.assertEquals(username, user.getPhone());
    }

    @Test
    public void testRoles() {
        Role role1 = new Role(ERole.ROLE_ADMIN);
        Role role2 = new Role(ERole.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);
        User user = new User();
        user.setRoles(roles);
        Assertions.assertEquals(roles, user.getRoles());
    }
}
