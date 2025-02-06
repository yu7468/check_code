package com.example.nagoyameshi.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;  
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;
	
	public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;  
        this.restaurantRepository = restaurantRepository;  
        this.userRepository = userRepository;  
    }    
	
	@Transactional
	public void submit(ReservationInputForm reservationInputForm) {
	    Reservation reservation = new Reservation();
	    Restaurant restaurant = restaurantRepository.getReferenceById(reservationInputForm.getRestaurantId());
	    User user = userRepository.getReferenceById(reservationInputForm.getUserId());
	    LocalDate checkDate = reservationInputForm.getCheckDate();

	    reservation.setRestaurant(restaurant);
	    reservation.setUser(user);
	    reservation.setCheckDate(checkDate);
	    reservation.setStartTime(reservationInputForm.getStartTime());
	    reservation.setFinishTime(reservationInputForm.getFinishTime());
	    reservation.setPeople(reservationInputForm.getPeople());

	    reservationRepository.save(reservation);
	}

}
