CREATE TABLE orders_polishing
(
    id                SERIAL PRIMARY KEY,
    name              VARCHAR(255),
    price_first_type  INTEGER,
    price_second_type INTEGER,
    price_third_type  INTEGER,
    time_first_type   INTEGER,
    time_second_type  INTEGER,
    time_third_type   INTEGER
);

INSERT INTO orders_polishing(name, price_first_type, price_second_type,
                             price_third_type, time_first_type, time_second_type,
                             time_third_type)
VALUES ('Полировка_восстановительная', 8000, 10000, 14000, 480, 480, 480),
       ('Глубокая_абразивная_полировка', 12000, 14000, 18000, 1680, 1680, 1680),
       ('Полировка_фар_1_шт.', 500, 500, 500, 20, 20, 20),
       ('Полимер_Sonax_до_6_месяцев', 1500, 1500, 1500, 300, 300, 300),
       ('Кварцекерамическое_покрытие_CAN_COAT_до_6_месяцев', 5000, 5000, 500, 480, 480, 480),
       ('Koch_Chemie_1K-NANO_1_год', 7000, 8000, 10000, 480, 480, 480),
       ('Профессиональное_покрытие_керамика_(2_слоя_+_1_слой)_до_3_лет',
        20000, 22000, 25000, 1440, 1440, 1440);

