package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp()
    {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void findByUsernameHappyPath()
    {
        String username = "test";
        Cart cart = new Cart();
        User user = new User();
        user.setId(0);
        user.setUsername(username);
        user.setPassword("Hashed");
        user.setCart(cart);
        when(userRepo.findByUsername("test")).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(username);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        user = response.getBody();
        Assert.assertEquals("test", user.getUsername());
        assertEquals(0, user.getId());
    }

    @Test
    public void createUsePasswordMismatch()
    {
        when(encoder.encode("password")).thenReturn("Hashed");
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("newtest");
        req.setPassword("password");
        req.setConfirmPassword("newpassword");
        final ResponseEntity<User> response = userController.createUser(req);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserHappyPath() throws Exception
    {
        when(encoder.encode("password")).thenReturn("Hashed");
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("password");
        req.setConfirmPassword("password");
        final ResponseEntity<User> response = userController.createUser(req);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("Hashed", user.getPassword());

    }
}