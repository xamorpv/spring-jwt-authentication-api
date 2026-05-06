package ru.ls.pjwt.domain.token.repository;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ls.pjwt.domain.token.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  Optional<RefreshToken> findByUuid(String uuid);

  /**
   * Deletes refresh tokens that satisfy either of two conditions.
   *
   * <ul>
   *   <li>the token was never used ({@code used = false}) and was created before the cutoff
   *   <li>the token has been used ({@code used = true}) and its usage occurred before the cutoff
   * </ul>
   *
   * @param time the cutoff instant (exclusive); tokens matching the conditions before this time are
   *     removed
   * @return the number of tokens deleted
   */
  @Modifying
  @Query(
"""
delete from RefreshToken rt
where (rt.usedAt is null and rt.createdAt < :time)
or (rt.used = true and rt.usedAt < :time)""")
  int deleteUsedBefore(@Param("time") Instant time);

  /**
   * Marks all unused refresh tokens of a user as compromised and used.
   *
   * <p>This is typically invoked when a token replay or race condition is detected. All tokens
   * owned by the given user that are still unused ({@code used = false}) are immediately flagged as
   * {@code compromised = true}, {@code used = true}, and their {@code usedAt} is set to the current
   * timestamp. The token version is incremented to support optimistic locking.
   *
   * @param username the username whose tokens should be compromised
   */
  @Modifying(clearAutomatically = true)
  @Query(
"""
update RefreshToken rt
set rt.compromised = true, rt.used = true,
rt.usedAt = CURRENT_TIMESTAMP, rt.version = rt.version + 1
where rt.user.username = :username and rt.used = false""")
  void useAndCompromiseTokensForUser(@Param("username") String username);
}
