package ru.nsu.carwash_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.nsu.carwash_server.models.Orders;

import javax.transaction.Transactional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE orders SET booked = :Booked, user_id = COALESCE(:UserId, user_id), price = COALESCE(:Price, price)\n" +
            "WHERE id = :OrderId", nativeQuery = true)
    void changeOrderToBooked(@Param("Booked") Boolean booked,@Param("UserId") Long userId,
                            @Param("OrderId") Long orderId,@Param("Price") Double price);
}
