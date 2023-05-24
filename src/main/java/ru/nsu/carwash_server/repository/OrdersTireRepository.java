package ru.nsu.carwash_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.nsu.carwash_server.models.OrdersTire;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface OrdersTireRepository extends JpaRepository<OrdersTire, Long> {
    Optional<OrdersTire> findByName(String name);

    @Modifying
    @Transactional
    @Query(value = "UPDATE orders_tire SET  price_r_13= COALESCE(:PriceR13, price_r_13)," +
            " price_r_14 = COALESCE(:PriceR14, price_r_14), price_r_15 = COALESCE(:PriceR15, price_r_15)," +
            " price_r_16 = COALESCE(:PriceR16,price_r_16),price_r_17 = COALESCE(:PriceR17,price_r_17)," +
            "price_r_18 = COALESCE(:PriceR18,price_r_18), price_r_19 = COALESCE(:PriceR19,price_r_19)," +
            " price_r_20 = COALESCE(:PriceR20,price_r_20), price_r_21 = COALESCE(:PriceR21,price_r_21)," +
            "price_r_22 = COALESCE(:PriceR22,price_r_22), role = COALESCE(:Role,role)" +
            "WHERE name = :Name", nativeQuery = true)
    void updateTireOrderInfo(@Param("Name") String name, @Param("PriceR13") Integer priceR13, @Param("PriceR14") Integer priceR14,
                             @Param("PriceR15") Integer priceR15, @Param("PriceR16") Integer priceR16, @Param("PriceR17") Integer priceR17,
                             @Param("PriceR18") Integer priceR18, @Param("PriceR19") Integer priceR19,
                             @Param("PriceR20") Integer priceR20, @Param("PriceR21") Integer priceR21,
                             @Param("PriceR22") Integer priceR22, @Param("Role") String role);

    @Transactional
    @Query(value = "SELECT name FROM orders_tire", nativeQuery = true)
    Optional<List<String>> getActualOrders();
}