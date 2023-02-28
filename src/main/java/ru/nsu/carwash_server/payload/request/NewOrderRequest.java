package ru.nsu.carwash_server.payload.request;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class NewOrderRequest {

    @NotNull
    private Double price;

    @NotBlank
    private String name;

    private Date date;

    private boolean booked;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }


    public void setBooked(boolean booked) {
        this.booked = booked;
    }

}
