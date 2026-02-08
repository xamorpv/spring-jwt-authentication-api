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

    @Column(nullable = false, unique = true, name = "token", length = 128) // len = 128 для расширяемости
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "used")
    private Boolean used = false;

    @Column(name = "used_at")
    private Instant usedAt;

    public RefreshToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
