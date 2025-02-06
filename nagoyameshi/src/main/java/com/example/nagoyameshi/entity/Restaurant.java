package com.example.nagoyameshi.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "restaurants")
@Data

public class Restaurant {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "image_name", length = 255)
    private String imageName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "min_price")
    private Integer minPrice;

    @Column(name = "max_price")
    private Integer maxPrice;

    @Column(name = "open")
    private String open;

    @Column(name = "close")
    private String close;

    @Column(name = "closed_day")
    private String closedDay;

    @Column(name = "postal_code", nullable = false, length = 8)
    private String postalCode;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    
    public LocalTime getOpenTime() {
        if (open == null || open.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(open);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid open time format: " + open);
        }
    }

    public LocalTime getCloseTime() {
        if (close == null || close.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(close);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid close time format: " + close);
        }
    }
    
    @ManyToMany
    @JoinTable(
        name = "restaurant_categories",
        joinColumns = @JoinColumn(name = "restaurant_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
    
    public String getCategoriesName() {
        return categories.stream()
                         .map(Category::getName)
                         .collect(Collectors.joining(", "));
    }
    

}
