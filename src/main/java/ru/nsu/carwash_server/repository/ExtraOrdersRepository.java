package ru.nsu.carwash_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.carwash_server.models.OrdersAdditional;
import ru.nsu.carwash_server.models.constants.EOrderAdditional;

import java.util.Optional;

@Repository
public interface ExtraOrdersRepository extends JpaRepository<OrdersAdditional, Long> {
    Optional<OrdersAdditional> findByName(EOrderAdditional name);

}
