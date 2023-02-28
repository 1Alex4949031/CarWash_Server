package ru.nsu.carwash_server.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.nsu.carwash_server.models.RefreshToken;
import ru.nsu.carwash_server.models.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  @Modifying
  int deleteByUser(User user);
}
