package ru.ls.pjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ls.pjwt.entity.RefreshToken;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUuid(String uuid);

    @Query("select rt from RefreshToken rt where rt.user.username = :username and rt.used = false")
    List<RefreshToken> findActiveByUsername(@Param("username") String username);

    @Modifying
    @Query("delete from RefreshToken rt where rt.used = true and rt.usedAt < time")
    void deleteUsedBefore(@Param("time") Instant time);
}
