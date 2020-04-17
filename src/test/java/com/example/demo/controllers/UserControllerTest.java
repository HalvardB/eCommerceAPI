package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private User defaultUser;

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        defaultUser = new User();
        defaultUser.setUsername("test");
        defaultUser.setPassword("testPw");
        defaultUser.setId(0L);
    }

    @Test
    public void create_user_happy_path() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void create_user_pw_not_matching() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("otherPassword");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void create_user_pw_too_short() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("pw");
        request.setConfirmPassword("pw");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_id_happy_path() {
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.of(defaultUser));
        ResponseEntity<User> responseId = userController.findById(0L);
        assertEquals(200, responseId.getStatusCodeValue());
        User user = responseId.getBody();
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
    }

    @Test
    public void find_user_by_id_error_path() {
        ResponseEntity<User> responseId = userController.findById(1L);
        assertEquals(404, responseId.getStatusCodeValue());
    }

    @Test
    public void find_user_by_name_happy_path() {
        when(userRepository.findByUsername("test")).thenReturn(defaultUser);
        ResponseEntity<User> responseUserName = userController.findByUserName("test");
        assertEquals(200, responseUserName.getStatusCodeValue());
        User user2 = responseUserName.getBody();
        assertEquals(0, user2.getId());
        assertEquals("test", user2.getUsername());
    }

    @Test
    public void find_user_by_name_error_path() {
        ResponseEntity<User> responseUserName = userController.findByUserName("test");
        assertEquals(404, responseUserName.getStatusCodeValue());
    }
}
