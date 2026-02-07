package ru.ls.pjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ls.pjwt.entity.Authority;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    @Query("select a from Authority a left join fetch a.users where a.authority = :name")
    Optional<Authority> findByName(@Param("name") String name);
}
