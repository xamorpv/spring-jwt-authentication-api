package ru.ls.pjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ls.pjwt.entity.Authority;

public interface RoleRepository extends JpaRepository<Authority, Long> {
}
