package ru.ls.pjwt.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
