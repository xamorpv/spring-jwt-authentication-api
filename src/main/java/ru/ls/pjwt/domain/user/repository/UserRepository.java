package ru.ls.pjwt.domain.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ls.pjwt.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  @Query("select u from User u left join fetch u.authorities where u.username = :username")
  Optional<User> findByUsername(@Param("username") String username);
}
