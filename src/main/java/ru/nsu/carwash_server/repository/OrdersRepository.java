package ru.nsu.carwash_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.carwash_server.models.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    /*@Modifying
    @Query("UPDATE order SET booked = ?1, user_id = ?2 where id = ?3")
    int changeOrderToBooked(Boolean booked, Long userId, Long orderId);*/
}
