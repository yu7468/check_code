package com.example.samuraitravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.samuraitravel.service.ReviewService;

@Controller
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/all")
    public String listAllReviews(Model model) {
//        List<Review> allReviews = reviewService.findAll(); 
//        model.addAttribute("reviews", allReviews);
        return "reviews/list"; 
    }
}
