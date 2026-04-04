package ru.ls.pjwt.domain.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ls.pjwt.domain.token.entity.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUuid(String uuid);

    @Modifying
    @Query("delete from RefreshToken rt where (rt.usedAt is null and rt.createdAt < :time) or (rt.used = true and rt.usedAt < :time)")
    int deleteUsedBefore(@Param("time") Instant time);

    @Modifying(clearAutomatically = true)
    @Query("update RefreshToken rt set rt.compromised = true, rt.used = true, rt.usedAt = CURRENT_TIMESTAMP, rt.version = rt.version + 1" +
            " where rt.user.username = :username and rt.used = false")
    void useAndCompromiseTokensForUser(@Param("username") String username);
}
