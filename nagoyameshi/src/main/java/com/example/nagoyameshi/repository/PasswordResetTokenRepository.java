package com.example.nagoyameshi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.entity.User;


public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
 
	PasswordResetToken findByToken(String token);
 
	void deleteByUser(User user);
  
}
