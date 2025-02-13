package com.example.futurefridgesapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class UserUnitTest {

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("securePassword123");
        user.setPasscode("1234");
        user.setRole("admin");
    }

    @Test
    public void testGetEmail() {
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    public void testGetPassword() {
        assertEquals("securePassword123", user.getPassword());
    }

    @Test
    public void testGetPasscode() {
        assertEquals("1234", user.getPasscode());
    }

    @Test
    public void testGetRole() {
        assertEquals("admin", user.getRole());
    }

    @Test
    public void testSetEmail() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    public void testSetPassword() {
        user.setPassword("newPassword456");
        assertEquals("newPassword456", user.getPassword());
    }

    @Test
    public void testSetPasscode() {
        user.setPasscode("5678");
        assertEquals("5678", user.getPasscode());
    }

    @Test
    public void testSetRole() {
        user.setRole("chef");
        assertEquals("chef", user.getRole());
    }

    @Test
    public void testObjectCreation() {
        User newUser = new User();
        assertNotNull(newUser);
    }
}
