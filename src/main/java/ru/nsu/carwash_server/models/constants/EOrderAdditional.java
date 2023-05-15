package ru.nsu.carwash_server.models.constants;

import lombok.Getter;
import lombok.ToString;
import ru.nsu.carwash_server.models.OrderInfo;

@Getter
@ToString
public enum EOrderAdditional {

    TURBO_DRYING(new OrderInfo("Турбо сушка кузова", 25, 550,
            600, 660)),

    BODY_BLOW(new OrderInfo("Продувка кузова", 7,
            110, 140, 160)),

    LOCKS_AND_MIRRORS_BLOW(new OrderInfo("Продувка замков, зеркал",
            5, 70)),

    SILICONE_TREATMENT(new OrderInfo("Обработка силиконом", 5,
            110, 140, 160)),

    LOCK_FLUID_TREATMENT(new OrderInfo("Обработка замков жидкостью",
            2, 70)),

    LEATHER_CONDITIONER_TREATMENT(new OrderInfo("Обработка кожи кондиционером 1 эл.",
            7, 110)),

    SALON_PLASTIC_POLISH(new OrderInfo("Полироль пластика салона", 5,
            140, 150, 180)),

    PANEL_PLASTIC_POLISH(new OrderInfo("Полироль пластика панель", 3,
            80, 100, 100)),

    TRUNK_PLASTIC_POLISH(new OrderInfo("Полироль пластика багажник", 3,
            60, 80, 90)),

    RADIATOR_WASH(new OrderInfo("Наружная мойка радиатора", 0,
            90, 110, 170)), //не указано время, поэтому 0

    TIRE_BLACKENING(new OrderInfo("Чернение шин 4 штуки", 3, 110,
            170, 170)),

    SALON_OZONATION(new OrderInfo("Озонирование салона", 30, 1100)),

    CLEANING_BITUMEN_STAINS_BODY(new OrderInfo("Очистка битумных пятен кузова",
            30, 1100, 1650, 1650)),

    WINDSHIELD_NANO_GLASS_COATING(new OrderInfo("Покрытие лобового стекла Nano Glass 1 элемент",
            15, 550, 660, 770)),

    SIDE_GLASS_NANO_GLASS_COATING(new OrderInfo("Покрытие бокового стекла Nano Glass 1 элемент",
            15, 220, 280, 330)),
    COMPLEX_ALL_GLASSES(new OrderInfo("Комплекс всех стёкол", 30,
            1650, 2200, 2750)),

    ENGINE_CHEMICAL_CLEANING(new OrderInfo("Диэлектрическая химчистка двигателя",
            40, 900, 1000, 1100)),

    DISC_CHEMICAL_CLEANING(new OrderInfo("Химчистка дисков 4 штуки", 20,
            660, 900, 1100)),

    TRUNK_CHEMICAL_CLEANING(new OrderInfo("Химчистка багажника", 120,
            180, 300,
            1100, 1650, 1650)),

    DOOR_CHEMICAL_CLEANING(new OrderInfo("Химчистка двери 1 элемент",
            20,
            30, 40, 550, 550, 550)),

    TEXTILE_SEAT_CHEMICAL_CLEANING(new OrderInfo("Химчистка кресло (текстиль) 1 элемент", 20,
            30, 40, 550, 550, 550)),

    LEATHER_SEAT_CHEMICAL_CLEANING(new OrderInfo("Химчистка кресло (кожа) 1 элемент", 15,
            25, 30, 550, 550, 550)),

    FRONT_PANEL_CHEMICAL_CLEANING(new OrderInfo("Химчистка передней панели", 20,
            35, 60, 1100, 1100, 1650)),

    FLOOR_CHEMICAL_CLEANING(new OrderInfo("Химчистка пола", 3, 5,
            8, 1100, 1650, 1650)),

    CEILING_CHEMICAL_CLEANING(new OrderInfo("Химчистка потолка", 55, 80,
            120, 1000, 1650, 1650)),

    ONE_PHASE_WASH_WITH_CHEMICALS_NO_WIPING(new OrderInfo("Однофазная мойка с химией без протирки",
            20, 280, 330, 350)),

    TWO_PHASE_WASH_WITH_WIPING(new OrderInfo("Мойка кузова 2 фазы с протиркой", 40,
            470, 550, 600)),

    TWO_PHASE_WASH_NO_WIPING(new OrderInfo("Мойка кузова 2 фазы без протирки", 30,
            380, 460, 520)),

    COMPLEX_WASH_1_PHASES_SALON(new OrderInfo("Мойка КОМПЛЕКС (кузов + салон)", 40,
            50, 60, 680, 720, 820)),//?? по времени
    COMPLEX_WASH_2_PHASES_SALON(new OrderInfo("Мойка КОМЛПЕКС (кузов 2 фазы + салон)", 60,
            70, 80, 900, 1000, 1200)),

    QUARTZ_COATING(new OrderInfo("Покрытие кварцевой защитой", 5,
            660, 720, 770)),

    ENGINE_WASH_WITH_CHEMICALS_AND_DRYING(new OrderInfo("Мойка двигателя с химическим раствором + сушка",
            20, 440, 500, 500)),

    WHEEL_ARCHES_CLEANING(new OrderInfo("Очистка арок колес", 15,
            130, 180, 220)),

    TRUNK_CLEANING(new OrderInfo("Уборка багажника", 20, 170,
            220, 280)),

    INNER_WET_CLEANING(new OrderInfo("Влажная уборка салона", 20,
            160, 210, 270)),

    FRONT_PANEL_WET_CLEANING(new OrderInfo("Влажная уборка передней панели", 10,
            90, 110, 110)),

    VACUUM_CLEANING_SALON(new OrderInfo("Пылесос салона", 20, 160,
            210, 270)),

    VACUUM_CLEANING_FLOOR(new OrderInfo("Пылесос пола", 15, 100,
            110, 150)),

    VACUUM_CLEANING_CARPETS(new OrderInfo("Пылесос ковриков", 10, 60,
            70, 90)),

    CARGO_MAT(new OrderInfo("Коврик багажников", 5, 70)),

    RUBBER_MAT_1(new OrderInfo("Резиновый коврик 1 шт", 3, 20)),

    TEXTILE_MAT_WASH(new OrderInfo("Стирка текстильного коврика за 1 шт", 5, 40)),

    GLASS_CLEANING_2_SIDES(new OrderInfo("Чистка стёкол с 2 сторон", 20,
            170, 220, 270)),

    INNER_GLASS_CLEANING(new OrderInfo("Чистка стёкол внутри салона", 15,
            160, 210, 270)),

    WINDSHIELD_CLEANING(new OrderInfo("Чистка ветрового стекла", 5, 80,
            100, 150)),
    POLISHING_RESTORATION(new OrderInfo("Полировка восстановительная", 480,
            8000, 10000, 14000)),
    DEEP_ABRASIVE_POLISHING(new OrderInfo("Глубокая абразивная полировка", 1680,
            12000, 14000, 18000)),
    HEADLINE_POLISHING(new OrderInfo("Полировка фар 1 шт", 20, 500)),
    PROFESSIONAL_SONIX_POLYMER_COATING(new OrderInfo("Полимер Sonax до 6 месяцев", 300,
            1500)),
    PROFESSIONAL_QUARTZ_CERAMIC_COATING(new OrderInfo("Кварцекерамическое покрытие CAN COAT до 6 месяцев",
            480, 5000)),
    PROFESSIONAL_KOCH_CHEMIE_1K_NANO(new OrderInfo("Koch Chemie 1K-NANO 1 год",
            480, 7000, 8000, 10000)),
    PROFESSIONAL_CERAMIC_COATING_2_LAYERS_ONE_LAYER(new OrderInfo("Профессиональное покрытие керамика (2 слоя + 1 слой) до 3 лет",
            1440, 20000, 22000, 25000));


    private final OrderInfo orderInfo;

    EOrderAdditional(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }
}