package ru.ls.pjwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@ToString
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ToString.Exclude
    @Column(nullable = false, unique = true, name = "token", length = 128) // len = 128 для расширяемости
    private String token;

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

    public RefreshToken(String token, String uuid, User user) {
        this.token = token;
        this.user = user;
        this.uuid = uuid;
    }
}
