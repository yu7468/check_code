package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Controller
@RequestMapping("/restaurants")

public class RestaurantController {
	private RestaurantRepository restaurantRepository;
	
	public RestaurantController(RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
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
    public String show(@PathVariable(name = "id") Integer id, Model model) {
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        
        model.addAttribute("restaurant", restaurant);     
        
        return "restaurants/show";
    } 



}
