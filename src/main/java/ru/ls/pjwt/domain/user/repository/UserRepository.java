package ru.ls.pjwt.domain.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ls.pjwt.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  boolean existsByUsername(String username);

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  boolean existsByEmail(String email);

  /**
   * Finds a user by username, eagerly fetching their authorities.
   *
   * @param username the username to search for
   * @return the user with fully initialized {@code authorities} collection, or {@code
   *     Optional.empty()} if not found
   */
  @Query("select u from User u left join fetch u.authorities where u.username = :username")
  Optional<User> findByUsername(@Param("username") String username);
}
