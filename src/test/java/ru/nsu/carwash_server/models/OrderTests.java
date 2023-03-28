package ru.nsu.carwash_server.models;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTests {

    @Test
    public void testConstructor() {
        String name = "Test Order";
        double price = 10.0;
        Date date = new Date();
        Order order = new Order(name, price, date);
        assertEquals(order.getName(), name);
        assertEquals(order.getPrice(), price, 0.0);
        assertEquals(order.getDate(), date);
    }

    @Test
    public void testSetterGetter() {
        Order order = new Order();
        Long id = 1L;
        double price = 20.0;
        String name = "Test Order";
        boolean booked = true;
        User user = new User();
        user.setId(1L);
        order.setId(id);
        order.setPrice(price);
        order.setName(name);
        order.setBooked(booked);
        order.setUser(user);
        assertEquals(order.getId(), id);
        assertEquals(order.getPrice(), price, 0.0);
        assertEquals(order.getName(), name);
        assertEquals(order.isBooked(), booked);
        assertEquals(order.getUser(), user);
    }
}
