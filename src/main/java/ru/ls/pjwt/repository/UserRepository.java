package ru.ls.pjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ls.pjwt.entity.Authority;
import ru.ls.pjwt.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("select u from User u left join fetch u.authorities where username = :username")
    Optional<User> findByUsername(@Param("username") String username);
}
