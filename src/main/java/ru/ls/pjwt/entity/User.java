package ru.ls.pjwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true, name = "username", length = 48)
    private String username;
    @Column(nullable = false, unique = true, name = "email")
    private String email;
    @Column(name = "password", length = 128, nullable = false) // len 128 для расширяемости
    private String password;

    @Column(name = "account_non_expired")
    private boolean accountNonExpired = false;
    @Column(name = "account_non_locked")
    private boolean accountNonLocked = false;
    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired = false;
    @Column(name = "enabled")
    private boolean enabled = false;

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<RefreshToken> refreshToken;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "users_authorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<Authority> authorities = new HashSet<>();
}
