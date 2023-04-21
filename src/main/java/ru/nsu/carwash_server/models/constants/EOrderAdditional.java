package ru.nsu.carwash_server.models.constants;

import lombok.Getter;
import lombok.ToString;
import ru.nsu.carwash_server.models.OrderService;

@Getter
@ToString
public enum EOrderAdditional {

    TURBO_DRYING(new OrderService("Турбо сушка кузова", 25, 550,
            600, 660)),

    BODY_BLOW(new OrderService("Продувка кузова", 7, 110,
            140, 160)),

    LOCKS_AND_MIRRORS_BLOW(new OrderService("Продувка замков, зеркал", 5, 70)),

    SILICONE_TREATMENT(new OrderService("Обработка силиконом", 5, 110,
            140, 160)),

    LOCK_FLUID_TREATMENT(new OrderService("Обработка замков жидкостью", 2, 70)),

    LEATHER_CONDITIONER_TREATMENT(new OrderService("Обработка кожи кондиционером 1 эл.", 7,
            110)),

    SALON_PLASTIC_POLISH(new OrderService("Полироль пластика салона", 5,
            140, 150, 180)),

    PANEL_PLASTIC_POLISH(new OrderService("Полироль пластика панель", 3, 80,
            100, 100)),

    TRUNK_PLASTIC_POLISH(new OrderService("Полироль пластика багажник", 3,
            60, 80, 90)),

    RADIATOR_WASH(new OrderService("Наружная мойка радиатора", 0, 90,
            110, 170)), //не указано время, поэтому 0

    TIRE_BLACKENING(new OrderService("Чернение шин 4 штуки", 3, 110,
            170, 170)),

    SALON_OZONATION(new OrderService("Озонирование салона", 30, 1100)),

    CLEANING_BITUMEN_STAINS_BODY(new OrderService("Очистка битумных пятен кузова",
            30, 1100, 1650, 1650)),

    WINDSHIELD_NANO_GLASS_COATING(new OrderService("Покрытие лобового стекла Nano Glass 1 элемент",
            15, 550, 660, 770)),

    SIDE_GLASS_NANO_GLASS_COATING(new OrderService("Покрытие бокового стекла Nano Glass 1 элемент",
            15, 220, 280, 330)),

    ENGINE_CHEMICAL_CLEANING(new OrderService("Диэлектрическая химчистка двигателя", 40,
            900, 1000, 1100)),

    DISC_CHEMICAL_CLEANING(new OrderService("Химчистка дисков 4 штуки", 20, 660,
            900, 1100)),

    TRUNK_CHEMICAL_CLEANING(new OrderService("Химчистка багажника", 120, 180,
            300, 1100, 1650, 1650)),

    DOOR_CHEMICAL_CLEANING(new OrderService("Химчистка двери 1 элемент", 20,
            30, 40, 550, 550, 550)),

    TEXTILE_SEAT_CHEMICAL_CLEANING(new OrderService("Химчистка кресло (текстиль) 1 элемент", 20,
            30, 40, 550, 550, 550)),

    LEATHER_SEAT_CHEMICAL_CLEANING(new OrderService("Химчистка кресло (кожа) 1 элемент", 15,
            25, 30, 550, 550, 550)),

    FRONT_PANEL_CHEMICAL_CLEANING(new OrderService("Химчистка передней панели", 20,
            35, 60, 1100, 1100, 1650)),

    FLOOR_CHEMICAL_CLEANING(new OrderService("Химчистка пола", 3, 5,
            8, 1100, 1650, 1650)),

    CEILING_CHEMICAL_CLEANING(new OrderService("Химчистка потолка", 55, 80,
            120, 1000, 1650, 1650));

    private final OrderService orderInfo;

    EOrderAdditional(OrderService orderInfo) {
        this.orderInfo = orderInfo;
    }
}