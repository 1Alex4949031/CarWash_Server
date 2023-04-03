package ru.nsu.carwash_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.nsu.carwash_server.models.BookedTime;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<BookedTime, Long> {

    @Query(value = "SELECT * FROM schedule WHERE date_trunc('day', start_time) =:Date",
            nativeQuery = true )
   List<BookedTime> checkDayByTime(@Param("Date")Date date);
}