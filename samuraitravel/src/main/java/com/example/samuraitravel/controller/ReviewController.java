package com.example.samuraitravel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;


@Controller
public class ReviewController {
	private final HouseRepository houseRepository; 
	private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewController(HouseRepository houseRepository, ReviewRepository reviewRepository) {
	    this.houseRepository = houseRepository;
	    this.reviewRepository = reviewRepository;
	}  

    @GetMapping("/reviews/list/{id}")
    public String listReviews(@PathVariable(name = "id") Integer id, Model model) {
        House house = houseRepository.getReferenceById(id);
        List<Review> reviews = reviewRepository.findByHouse(house);

        model.addAttribute("house", house);
        model.addAttribute("reviews", reviews);

        return "reviews/list";
    } 
    
    @GetMapping("/reviews/update/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        return "updateForm";
    }

    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable("id") long id) {
        return "redirect:/somePage";
    }
}
