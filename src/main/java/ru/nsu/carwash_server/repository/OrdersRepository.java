package ru.nsu.carwash_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nsu.carwash_server.models.Order;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Set;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE orders SET booked = :Booked, user_id = COALESCE(:UserId, user_id), " +
            "price = COALESCE(:Price, price), administrator = COALESCE(:Administrator, administrator)," +
            "auto_id = COALESCE(:AutoId, auto_id), box_number = COALESCE(:BoxNumber, box_number)," +
            "specialist = COALESCE(:Specialist, specialist), bonuses = COALESCE(:Bonuses, bonuses)," +
            "comments = COALESCE(:Comments, comments), executed = COALESCE(:Executed, executed)," +
            "start_time = COALESCE(:StartTime, start_time), end_time = COALESCE(:EndTime, end_time)" +
            "WHERE id = :OrderId", nativeQuery = true)
    void updateOrderInfo(@Param("Booked") Boolean booked, @Param("UserId") Long userId,
                         @Param("Price") Double price, @Param("AutoId") Long autoId,
                         @Param("Specialist") String specialist, @Param("Administrator") String administrator,
                         @Param("BoxNumber") int boxNumber, @Param("OrderId") Long orderId,
                         @Param("Bonuses") int bonuses, @Param("Comments") String comments,
                         @Param("Executed") Boolean executed, @Param("StartTime") Date startTime,
                         @Param("EndTime") Date endTime);

    //@Query(value = "SELECT * FROM orders WHERE start_time >= :StartTime " +
            //"AND end_time <= :EndTime", nativeQuery = true)
    //@Query(value = "SELECT * FROM orders WHERE start_time " +
    //        "BETWEEN :StartTime AND :EndTime" +
    //        " AND end_time BETWEEN :StartTime AND :EndTime", nativeQuery = true)
    //Set<Order> getBookedOrdersInOneDay(@Param("StartTime") Date startTime, @Param("EndTime") Date endTime);

    @Query(value = "SELECT * FROM orders WHERE start_time " +
            "BETWEEN :StartTime AND :EndTime " +    // Пробел добавлен здесь
            " AND end_time BETWEEN :StartTime AND :EndTime", nativeQuery = true)
    Set<Order> getBookedOrdersInTimeInterval(@Param("StartTime") Date startTime, @Param("EndTime") Date endTime);

    @Query(value = "SELECT * FROM orders WHERE start_time " +
            "BETWEEN :StartTime AND :EndTime", nativeQuery = true)
    Set<Order> getBookedOrdersInOneDay(@Param("StartTime") Date startTime, @Param("EndTime") Date endTime);
}
