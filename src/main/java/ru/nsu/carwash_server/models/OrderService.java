package ru.nsu.carwash_server.models;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class OrderService {

    private String name;

    private int timeForBodyType1;

    private int timeForBodyType2;

    private int timeForBodyType3;

    private int priceForBodyType1;

    private int priceForBodyType2;

    private int priceForBodyType3;

    public OrderService(String name, int theSameTime,
                        int priceForBodyType1, int priceForBodyType2, int priceForBodyType3) {
        this.name = name;
        this.timeForBodyType1 = theSameTime;
        this.timeForBodyType2 = theSameTime;
        this.timeForBodyType3 = theSameTime;
        this.priceForBodyType1 = priceForBodyType1;
        this.priceForBodyType2 = priceForBodyType2;
        this.priceForBodyType3 = priceForBodyType3;
    }

    public OrderService(String name, int theSameTime,
                        int theSamePrice) {
        this.name = name;
        this.timeForBodyType1 = theSameTime;
        this.timeForBodyType2 = theSameTime;
        this.timeForBodyType3 = theSameTime;
        this.priceForBodyType1 = theSamePrice;
        this.priceForBodyType2 = theSamePrice;
        this.priceForBodyType3 = theSamePrice;
    }

    public OrderService(String name, int timeForBodyType1, int timeForBodyType2, int timeForBodyType3,
                        int priceForBodyType1, int priceForBodyType2, int priceForBodyType3) {
        this.name = name;
        this.timeForBodyType1 = timeForBodyType1;
        this.timeForBodyType2 = timeForBodyType2;
        this.timeForBodyType3 = timeForBodyType3;
        this.priceForBodyType1 = priceForBodyType1;
        this.priceForBodyType2 = priceForBodyType2;
        this.priceForBodyType3 = priceForBodyType3;
    }

    public int getPriceForBodyType(int bodyType) {
        if (bodyType == 1) {
            return priceForBodyType1;
        } else if (bodyType == 2) {
            return priceForBodyType2;
        } else if (bodyType == 3) {
            return priceForBodyType3;
        } else {
            throw new IllegalArgumentException("Неверный класс автомобиля: " + bodyType);
        }
    }

    public int getTimeForBodyType(int bodyType) {
        if (bodyType == 1) {
            return timeForBodyType1;
        } else if (bodyType == 2) {
            return timeForBodyType2;
        } else if (bodyType == 3) {
            return timeForBodyType3;
        } else {
            throw new IllegalArgumentException("Неверный класс автомобиля: " + bodyType);
        }
    }
}