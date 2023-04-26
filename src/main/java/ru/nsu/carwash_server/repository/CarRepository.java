package ru.nsu.carwash_server.repository;


import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.carwash_server.models.Auto;
import ru.nsu.carwash_server.models.User;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Auto, Long> {

    @Override
    @NonNull
    Optional<Auto> findById(@NonNull Long aLong);

    Boolean existsByCarNumber(String carNumber);

    Optional<Auto> findByUser(User user);
}
