package com.microsv.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@Table(name = "invalidated_token")
@NoArgsConstructor
@AllArgsConstructor
public class InvalidatedToken {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "token")
    private String token;

    @Column(name = "expiry_time")
    private Date expiryTime;
}
