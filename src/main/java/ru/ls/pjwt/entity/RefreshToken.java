package ru.ls.pjwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {
    @ToString.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true, name = "token_uuid", length = 64)
    private String uuid;

    @ToString.Exclude
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
