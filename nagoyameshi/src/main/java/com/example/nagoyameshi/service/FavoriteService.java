package com.example.nagoyameshi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;

@Service

public class FavoriteService {
	
    private FavoriteRepository favoriteRepository;
	
    @Autowired
	public FavoriteService(FavoriteRepository favoriteRepository) {
		this.favoriteRepository = favoriteRepository;
	}
	
	@Transactional
    public void addFavorite(User user, Restaurant restaurant) {
        if (!favoriteRepository.existsByUserAndRestaurant(user, restaurant)) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setRestaurant(restaurant);
            favoriteRepository.save(favorite);
        }
    }
    
	@Transactional
    public void removeFavorite(User user, Restaurant restaurant) {
    	favoriteRepository.deleteByUserAndRestaurant(user, restaurant);
    }
	
	public Page<Favorite> findFavoritesByUser(User user, Pageable pageable) {
        return favoriteRepository.findByUser(user, pageable);
    }
	
	public boolean isFavorite(User user, Restaurant restaurant) {
        return favoriteRepository.existsByUserAndRestaurant(user, restaurant);
    }
}