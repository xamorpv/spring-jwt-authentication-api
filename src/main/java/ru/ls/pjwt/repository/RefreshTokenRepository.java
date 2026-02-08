package ru.ls.pjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ls.pjwt.entity.RefreshToken;

import java.time.Instant;
import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("select rt from RefreshToken rt where rt.user.username = :username")
    List<RefreshToken> findByUsername(@Param("username") String username);

    @Query("select rt from RefreshToken rt where rt.user.username = :username and rt.used = false")
    List<RefreshToken> findActiveByUsername(@Param("username") String username);

    @Modifying
    @Query("delete from RefreshToken rt where rt.used = true and rt.usedAt > :interval")
    void deleteUsedLater(@Param("interval") Instant interval);
}
