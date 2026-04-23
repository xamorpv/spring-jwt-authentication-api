package ru.ls.pjwt.domain.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ls.pjwt.domain.user.entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
  Optional<Authority> findByAuthority(String name);
}
