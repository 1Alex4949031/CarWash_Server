package ru.nsu.carwash_server.payload.request;

import ru.nsu.carwash_server.models.Orders;
import ru.nsu.carwash_server.models.User;

public class BookingOrderRequest {
    private User user;

    private Orders order;

    public User getUser() {
        return user;
    }

    public Orders getOrder() {
        return order;
    }
}
