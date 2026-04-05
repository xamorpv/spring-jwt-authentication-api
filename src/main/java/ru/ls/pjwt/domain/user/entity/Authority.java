package ru.ls.pjwt.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@ToString
@Entity
@Table(name = "authorities")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "authority")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "authority", unique = true, nullable = false)
    private String authority;

    @ToString.Exclude
    @ManyToMany(mappedBy = "authorities")
    private Set<User> users = new HashSet<>();
}