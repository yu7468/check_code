package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;

public class FavoriteController {
	private final HouseRepository houseRepository; 
	private final UserRepository userRepository;
	private final FavoriteRepository favoriteRepository;
	private final FavoriteService favoriteService;
	
	public FavoriteController(HouseRepository houseRepository, UserRepository userRepository, FavoriteRepository favoriteRepository, FavoriteService favoriteService) {
		this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.favoriteService = favoriteService;
	}
	
	@GetMapping("/favorites")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
        User user = userDetailsImpl.getUser();
        Page<Favorite> favoritePage = favoriteRepository.findByUser(user,pageable);
        
        model.addAttribute("favoritePage", favoritePage);

        return "favorites/index";
    } 
	
	@PostMapping("/favorites/add")
    public String addFavorite(@RequestParam Integer houseId, @RequestParam Integer userId, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        House house = houseRepository.findById(houseId).orElseThrow(() -> new IllegalArgumentException("Invalid house Id:" + houseId));

        favoriteService.addFavorite(user, house);

        redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加されました！");
        return "redirect:/houses/" + houseId; 
    }
	
	@PostMapping("/favorites/remove")
    public String removeFavorite(@RequestParam Integer houseId, @RequestParam Integer userId, RedirectAttributes redirectAttributes) {        
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
	    House house = houseRepository.findById(houseId).orElseThrow(() -> new IllegalArgumentException("Invalid house Id:" + houseId));
  
	    favoriteService.removeFavorite(user, house);
	    redirectAttributes.addFlashAttribute("successMessage", "お気に入りが解除されました！");
	    return "redirect:/houses/" + houseId;
    }
}

