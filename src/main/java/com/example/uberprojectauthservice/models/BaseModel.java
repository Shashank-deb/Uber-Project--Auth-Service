package com.example.uberprojectauthservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    protected Date updatedAt;
}

