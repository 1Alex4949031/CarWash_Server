package ru.nsu.carwash_server.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.carwash_server.models.Auto;

@Repository
public interface CarRepository extends JpaRepository<Auto, Long> {
}
