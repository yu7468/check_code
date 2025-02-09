package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;

@Controller
@RequestMapping("/restaurants")

public class RestaurantController {
	private RestaurantRepository restaurantRepository;
	private final ReviewRepository reviewRepository;
	private final FavoriteService favoriteService;
	
	public RestaurantController(RestaurantRepository restaurantRepository, FavoriteService favoriteService, ReviewRepository reviewRepository) {
		this.restaurantRepository = restaurantRepository;
		this.favoriteService = favoriteService;	
		this.reviewRepository = reviewRepository;
		}
	
	@GetMapping
	public String index(
	        @RequestParam(name = "keyword", required = false) String keyword,
	        @RequestParam(name = "category", required = false) String category,
	        @RequestParam(name = "maxPrice", required = false, defaultValue = "") String maxPriceParam,
	        @RequestParam(name = "order", required = false) String order,
	        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
	        Model model) {

	    Page<Restaurant> restaurantPage;
	    
	    Integer maxPrice = null;
	    if (!maxPriceParam.isEmpty()) {
	        try {
	            maxPrice = Integer.valueOf(maxPriceParam);
	        } catch (NumberFormatException e) {
	            maxPrice = null;
	        }
	    }

	    if (keyword != null && !keyword.isEmpty()) {
	        // 根据 keyword 搜索，并按 order 决定排序方式
	        if ("maxPriceAsc".equals(order)) {
	            restaurantPage = restaurantRepository.findByNameContainingOrAddressContainingOrderByMaxPriceAsc(keyword, keyword, pageable);
	        } else {
	            restaurantPage = restaurantRepository.findByNameContainingOrAddressContainingOrderByCreatedAtDesc(keyword, keyword, pageable);
	        }
	    } else if (maxPrice != null) {
	        // 根据 maxPrice 筛选，并按 order 决定排序方式
	        if ("maxPriceAsc".equals(order)) {
	            restaurantPage = restaurantRepository.findByMaxPriceLessThanEqualOrderByMaxPriceAsc(maxPrice, pageable);
	        } else {
	            restaurantPage = restaurantRepository.findByMaxPriceLessThanEqualOrderByCreatedAtDesc(maxPrice, pageable);
	        }
	    } else if (category != null && !category.isEmpty()) {
	        // 根据 category 筛选，并按 order 决定排序方式
	        if ("maxPriceAsc".equals(order)) {
	            restaurantPage = restaurantRepository.findByCategoryNameLikeOrderByMaxPriceAsc(category, pageable);
	        } else {
	            restaurantPage = restaurantRepository.findByCategoryNameLikeOrderByCreatedAtDesc(category, pageable);
	        }
	    } else {
	        // 不指定任何筛选条件，直接全量排序
	        if ("maxPriceAsc".equals(order)) {
	            restaurantPage = restaurantRepository.findAllByOrderByMaxPriceAsc(pageable);
	        } else {
	            restaurantPage = restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
	        }
	    }

	    // 将查询结果和筛选条件放入 Model
	    model.addAttribute("restaurantPage", restaurantPage);
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("category", category);
	    model.addAttribute("maxPrice", maxPrice);
	    model.addAttribute("order", order);

	    return "restaurants/index";

	}
	
	
	@GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, 
    		@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		Model model) {
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        List<Review> reviews = reviewRepository.findByRestaurant(restaurant);
        
        boolean isFavorite = false;
        if (userDetailsImpl != null) {
            User user = userDetailsImpl.getUser();
            isFavorite = favoriteService.isFavorite(user, restaurant);
        }
        
        model.addAttribute("restaurant", restaurant);  
        model.addAttribute("reviews", reviews);
        model.addAttribute("isFavorite", isFavorite);
        
        return "restaurants/show";
    } 



}
