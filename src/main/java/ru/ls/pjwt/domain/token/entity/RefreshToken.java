package ru.ls.pjwt.domain.token.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ls.pjwt.common.database.entity.TimestampedEntity;
import ru.ls.pjwt.domain.user.entity.User;

import java.time.Instant;

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

    public RefreshToken(String uuid, User user) {
        this.user = user;
        this.uuid = uuid;
    }
}
