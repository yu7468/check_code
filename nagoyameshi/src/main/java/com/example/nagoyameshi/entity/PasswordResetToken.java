package com.example.nagoyameshi.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "reset_tokens")

public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token", unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    private Timestamp createdAt;

    @LastModifiedDate
    private Timestamp updatedAt;

    @Transient
    private boolean expired;

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.expired = false; // 或设置有效期
    }
    
    public boolean isExpired() {
        // 24小时有效期
        long tokenAge = System.currentTimeMillis() - createdAt.getTime();
        return tokenAge > 24 * 60 * 60 * 1000;
    }

}
