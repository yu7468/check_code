package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Category;


public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);
    Page<Category> findByNameContaining(String keyword, Pageable pageable); // 改进
}

