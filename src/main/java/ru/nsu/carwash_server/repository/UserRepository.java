package ru.nsu.carwash_server.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nsu.carwash_server.models.User;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Modifying
  @Transactional
  @Query(value = "UPDATE users SET email = COALESCE(:Email, email), username = COALESCE(:Phone, username), " +
          "phone = COALESCE(:Phone, phone), fullName = COALESCE(:FullName, fullName)" +
          "WHERE id = :UserId", nativeQuery = true)
  int changeUserInfo(@Param("Email") String email, @Param("Phone") String username,
                     @Param("UserId") Long userId, @Param("FullName") String fullName);



  @Query(value = "SELECT * FROM users WHERE username = :username",
          nativeQuery = true)
  Optional<User> findByUsername(@Param("username") String username);

  Boolean existsByUsername(String username);

  @Override
  Optional<User> findById(Long aLong);

  Boolean existsByEmail(String email);
}
