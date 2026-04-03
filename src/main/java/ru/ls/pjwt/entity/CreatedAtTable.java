package ru.ls.pjwt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class CreatedAtTable {
    @CreatedDate
    @Setter(AccessLevel.NONE)
    @Getter
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
