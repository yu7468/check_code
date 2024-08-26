//package com.example.samuraitravel.service;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.example.samuraitravel.entity.House;
//import com.example.samuraitravel.entity.Review;
//import com.example.samuraitravel.entity.User;
//import com.example.samuraitravel.form.ReviewForm;
//import com.example.samuraitravel.repository.HouseRepository;
//import com.example.samuraitravel.repository.ReviewRepository;
//import com.example.samuraitravel.repository.UserRepository;
//
//@Service
//public class ReviewService {
//	private final ReviewRepository reviewRepository;
//	private final HouseRepository houseRepository;
//    private final UserRepository userRepository;
//	
//	public ReviewService(ReviewRepository reviewRepository, HouseRepository houseRepository, UserRepository userRepository) {
//		this.reviewRepository = reviewRepository;
//		this.houseRepository = houseRepository;
//        this.userRepository = userRepository;
//	}
//	
//	@Transactional
//	public void create(ReviewForm reviewForm, Integer houseId, String userName) {
//		Review review = new Review();
//		
//		review.setRank_star(reviewForm.getRank_star());
//		review.setReview(reviewForm.getReview());
//		review.setReviewedAt(Timestamp.valueOf(LocalDateTime.now()));
//		
//		House house = houseRepository.findById(houseId);
//		User user = userRepository.findAll(userId);
//		
//		review.setHouse(house);
//		review.setUser(user);
//		
//		reviewRepository.save(review);
//	}
//	
//
//}
