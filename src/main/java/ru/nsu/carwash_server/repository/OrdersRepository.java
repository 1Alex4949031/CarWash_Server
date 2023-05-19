package ru.nsu.carwash_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nsu.carwash_server.models.Order;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE orders SET user_id = COALESCE(:UserId, user_id), " +
            "price = COALESCE(:Price, price), administrator = COALESCE(:Administrator, administrator)," +
            "auto_number = COALESCE(:AutoNumber, auto_number), box_number = COALESCE(:BoxNumber, box_number)," +
            "specialist = COALESCE(:Specialist, specialist), bonuses = COALESCE(:Bonuses, bonuses)," +
            "comments = COALESCE(:Comments, comments), executed = COALESCE(:Executed, executed)," +
            "order_type = COALESCE(:OrderType, order_type), start_time = COALESCE(:StartTime, start_time)," +
            "end_time = COALESCE(:EndTime, end_time)" +
            "WHERE id = :OrderId", nativeQuery = true)
    void updateOrderInfo(@Param("UserId") Long userId,
                         @Param("Price") Integer price, @Param("AutoNumber") String autoNumber,
                         @Param("Specialist") String specialist, @Param("Administrator") String administrator,
                         @Param("BoxNumber") Integer boxNumber, @Param("OrderId") Long orderId,
                         @Param("Bonuses") Integer bonuses, @Param("Comments") String comments,
                         @Param("Executed") Boolean executed, @Param("StartTime") Date startTime,
                         @Param("EndTime") Date endTime, @Param("OrderType") String orderType);

    @Query(value = "SELECT * FROM orders WHERE start_time " +
            "BETWEEN :StartTime AND :EndTime " +
            " AND end_time BETWEEN :StartTime AND :EndTime", nativeQuery = true)
    List<Order> getBookedOrdersInTimeIntervalFull(@Param("StartTime") Date startTime, @Param("EndTime") Date endTime);

    @Query(value = "SELECT * FROM orders WHERE start_time " +
            "BETWEEN :StartTime AND :EndTime", nativeQuery = true)
    List<Order> getBookedOrdersInOneDayFull(@Param("StartTime") Date startTime, @Param("EndTime") Date endTime);
}
