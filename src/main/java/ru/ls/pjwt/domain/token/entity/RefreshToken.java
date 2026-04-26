package ru.ls.pjwt.domain.token.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ls.pjwt.common.database.entity.TimestampedEntity;
import ru.ls.pjwt.domain.user.entity.User;

@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor
@Getter
@Setter
public class RefreshToken extends TimestampedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(nullable = false, unique = true, name = "token_uuid", length = 64)
  private String uuid;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "used", nullable = false)
  private boolean used = false;

  @Column(name = "compromised", nullable = false)
  private boolean compromised = false;

  @Column(name = "used_at")
  private Instant usedAt;

  @Version
  @Column(name = "version")
  private Integer version;

  /**
   * Creates a new refresh token associated with the given user and UUID.
   *
   * @param uuid unique token identifier
   * @param user the user who owns this token
   */
  public RefreshToken(String uuid, User user) {
    this.user = user;
    this.uuid = uuid;
  }
}
