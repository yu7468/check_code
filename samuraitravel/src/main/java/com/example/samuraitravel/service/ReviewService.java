package com.example.samuraitravel.service;

import org.springframework.stereotype.Service;

import com.example.samuraitravel.repository.ReviewRepository;


@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}

	

	

}
