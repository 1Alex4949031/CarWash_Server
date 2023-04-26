package ru.nsu.carwash_server.models.constants;


import lombok.Getter;
import lombok.ToString;
import ru.nsu.carwash_server.models.OrderService;

@Getter
@ToString
public enum EOrderMain {

    ONE_PHASE_WASH_WITH_CHEMICALS_NO_WIPING(new OrderService("Однофазная мойка с химией без протирки",
            20, 280, 330, 350)),

    TWO_PHASE_WASH_WITH_WIPING(new OrderService("Мойка кузова 2 фазы с протиркой", 40,
            470, 550, 600)),

    TWO_PHASE_WASH_NO_WIPING(new OrderService("Мойка кузова 2 фазы без протирки", 30,
            380, 460, 520)),

    COMPLEX_WASH_2_PHASES_SALON(new OrderService("Мойка комплекс 2 фазы + салон", 40,
            50, 60, 680, 720, 820)),//?? по времени

    QUARTZ_COATING(new OrderService("Покрытие кварцевой защитой", 5,
            660, 720, 770)),

    ENGINE_WASH_WITH_CHEMICALS_AND_DRYING(new OrderService("Мойка двигателя с химическим раствором + сушка",
            20, 440, 500, 500)),

    WHEEL_ARCHES_CLEANING(new OrderService("Очистка арок колес", 15,
            130, 180, 220)),

    TRUNK_CLEANING(new OrderService("Уборка багажника", 20, 170,
            220, 280)),

    INNER_WET_CLEANING(new OrderService("Влажная уборка багажника", 20,
            160, 210, 270)),

    FRONT_PANEL_WET_CLEANING(new OrderService("Влажная уборка передней панели", 10,
            90, 110, 110)),

    VACUUM_CLEANING_SALON(new OrderService("Пылесос салона", 20, 160,
            210, 270)),

    VACUUM_CLEANING_FLOOR(new OrderService("Пылесос пола", 15, 100,
            110, 150)),

    VACUUM_CLEANING_CARPETS(new OrderService("Пылесос ковриков", 10, 60,
            70, 90)),

    CARGO_MAT(new OrderService("Коврик багажников", 5, 70)),

    RUBBER_MAT_1(new OrderService("Резиновый коврик 1 шт", 3, 20)),

    TEXTILE_MAT_WASH(new OrderService("Стирка текстильного коврика за 1 шт", 5, 40)),

    GLASS_CLEANING_2_SIDES(new OrderService("Чистка стёкол с 2 сторон", 20,
            170, 220, 270)),

    INNER_GLASS_CLEANING(new OrderService("Чистка стёкол внутри салона", 15,
            160, 210, 270)),

    WINDSHIELD_CLEANING(new OrderService("Чистка ветрового стекла", 5, 80,
            100, 150));

    private final OrderService orderInfo;

    EOrderMain(OrderService orderInfo) {
        this.orderInfo = orderInfo;
    }
}
