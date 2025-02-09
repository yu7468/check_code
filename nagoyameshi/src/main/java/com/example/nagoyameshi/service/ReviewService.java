package com.example.nagoyameshi.service;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewPostForm;
import com.example.nagoyameshi.repository.ReviewRepository;


@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}
	
	
	@Transactional
	public Review create(ReviewPostForm reviewPostForm, Restaurant restaurant, User user) {
	    Review review = new Review();
	    
	    review.setRankStar(reviewPostForm.getRankStar());
	    review.setReview(reviewPostForm.getReview());
	    review.setRestaurant(restaurant);  
	    review.setUser(user); 
	    
	    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        review.setReviewedAt(currentTimestamp);
        review.setUpdatedAt(currentTimestamp);
	    
	    return reviewRepository.save(review);
	}


	@Transactional
    public void update(ReviewEditForm reviewEditForm) {
        Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
        
        review.setRankStar(reviewEditForm.getRankStar());
        review.setReview(reviewEditForm.getReview());
        review.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
  
        reviewRepository.save(review);
    }
	
	@Transactional
    public void delete(Integer id) {
		reviewRepository.deleteById(id);
    }
	
	public boolean hasUserReviewedRestaurant(Integer restaurantId, Integer userId) {
	    return reviewRepository.existsByRestaurantIdAndUserId(restaurantId, userId);
	}

	

}
