package com.example.samuraitravel.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.UserRepository;

public class FavoriteService {
	@Autowired
    private FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	
	public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository) {
		this.favoriteRepository = favoriteRepository;
		this.userRepository = userRepository;
	}

    public void addFavorite(User user, House house) {
        if (!favoriteRepository.existsByUserAndHouse(user, house)) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setHouse(house);
            favoriteRepository.save(favorite);
        }
    }
    
    public void removeFavorite(User user, House house) {
    	favoriteRepository.deleteByUserAndHouse(user, house);
    }
}
