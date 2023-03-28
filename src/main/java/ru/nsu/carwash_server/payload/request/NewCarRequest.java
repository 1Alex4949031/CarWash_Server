package ru.nsu.carwash_server.payload.request;


import lombok.Getter;
import lombok.Setter;
import ru.nsu.carwash_server.models.User;

@Getter @Setter
public class NewCarRequest {
    private String carNumber;
    private String carClass;
    private User user;
}
