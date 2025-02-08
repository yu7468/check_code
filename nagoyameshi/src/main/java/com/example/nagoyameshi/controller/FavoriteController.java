package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;

@Controller
public class FavoriteController {
	private final RestaurantRepository restaurantRepository; 
	private final UserRepository userRepository;
	private final FavoriteRepository favoriteRepository;
	private final FavoriteService favoriteService;
	
	public FavoriteController(RestaurantRepository restaurantRepository, UserRepository userRepository, FavoriteRepository favoriteRepository, FavoriteService favoriteService) {
		this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.favoriteService = favoriteService;
	}
	@GetMapping("/vip/favorites/index")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 6, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
        User user = userDetailsImpl.getUser();
        Page<Favorite> favoritePage = favoriteService.findFavoritesByUser(user,pageable);
        
        model.addAttribute("favoritePage", favoritePage);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", favoritePage.getTotalPages());

        return "vip/favorites/index";
    } 
	
	@PostMapping("/vip/favorites/add")
	public String addFavorite(
	    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
	    @RequestParam Integer restaurantId, 
	    RedirectAttributes redirectAttributes) {

	    User user = userDetailsImpl.getUser();
	    Restaurant restaurant = restaurantRepository.findById(restaurantId)
	        .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id: " + restaurantId));

	    favoriteService.addFavorite(user, restaurant);
	    redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加されました！");
	    
	    return "redirect:/restaurants/" + restaurantId; 
	}

	@PostMapping("/vip/favorites/remove")
	public String removeFavorite(
	    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
	    @RequestParam Integer restaurantId, 
	    RedirectAttributes redirectAttributes) {

	    User user = userDetailsImpl.getUser(); 
	    Restaurant restaurant = restaurantRepository.findById(restaurantId)
	        .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id: " + restaurantId));

	    favoriteService.removeFavorite(user, restaurant);
	    redirectAttributes.addFlashAttribute("successMessage", "お気に入りが解除されました！");
	    
	    return "redirect:/restaurants/" + restaurantId;
	}


}
