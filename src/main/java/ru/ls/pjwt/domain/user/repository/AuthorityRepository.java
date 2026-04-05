package ru.ls.pjwt.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ls.pjwt.domain.user.entity.Authority;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByAuthority(String name);
}
