package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewPostForm;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReviewService;


@Controller
@RequestMapping("/vip/reviews")
public class ReviewController {
	private final RestaurantRepository restaurantRepository; 
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;

    @Autowired
    public ReviewController(RestaurantRepository restaurantRepository, UserRepository userRepository, ReviewRepository reviewRepository, ReviewService reviewService) {
	    this.restaurantRepository = restaurantRepository;
	    this.userRepository = userRepository;
	    this.reviewRepository = reviewRepository;
	    this.reviewService = reviewService;
	}  
    
    @GetMapping("/post/{restaurantId}")
    public String post(@PathVariable("restaurantId") Integer restaurantId, 
                       @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
                       Model model, 
                       RedirectAttributes redirectAttributes) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant ID: " + restaurantId));
        User user = userDetailsImpl.getUser();

        boolean hasReviewed = reviewService.hasUserReviewedRestaurant(restaurantId, user.getId());
        if (hasReviewed) {
            redirectAttributes.addFlashAttribute("errorMessage", "すでにこの宿にレビューを投稿しています。");
            return "redirect:/vip/reviews/" + restaurantId + "/index";  // 进行 redirect
        }

        ReviewPostForm reviewPostForm = new ReviewPostForm(restaurant.getId(), user.getId(), null, "");
        model.addAttribute("reviewPostForm", reviewPostForm);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("user", user);
        return "vip/reviews/post"; 
    }


    @PostMapping("/{restaurantId}/create")
    public String create(@PathVariable Integer restaurantId,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         @ModelAttribute @Validated ReviewPostForm reviewPostForm, 
                         BindingResult bindingResult, 
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "vip/reviews/post"; 
        }

        User user = userDetailsImpl.getUser();
        Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);

        if (reviewService.hasUserReviewedRestaurant(restaurantId, user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "すでにこの宿にレビューを投稿しています。");
            return "redirect:/vip/reviews/" + restaurantId + "/index";
        }

        reviewService.create(reviewPostForm, restaurant, user);
        redirectAttributes.addFlashAttribute("successMessage", "レビューが投稿されました。");

        return "redirect:/vip/reviews/" + restaurantId + "/index";  // 这里修改跳转路径
    }



    @GetMapping("/{restaurantId}/index")
    public String index(@PathVariable(name = "restaurantId") int restaurantId, Model model, @PageableDefault(page = 0, size = 10, sort = "restaurantId", direction = Direction.ASC)Pageable pageable) {

        Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
        Page<Review> reviewPage = reviewRepository.findByRestaurant(restaurant,pageable);
        
        List<Review> reviews = reviewRepository.findByRestaurant(restaurant);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("reviews", reviews);

        return "vip/reviews/index";
    } 
    

    
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") Integer id, Model model) {        
        Review review = reviewRepository.getReferenceById(id);
        Restaurant restaurant = restaurantRepository.getReferenceById(review.getRestaurant().getId());
        ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(), review.getRankStar(), review.getReview());
        
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("reviewEditForm", reviewEditForm);        
        return "vip/reviews/edit";
    }    

    @PostMapping("/update")
        public String update(@ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    	if (bindingResult.hasErrors()) {
            return "reviews/edit";
        }
    	reviewService.update(reviewEditForm);
            redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
            
            return "redirect:/";
        }
    
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {        
        Review review = reviewRepository.getReferenceById(id); // 获取评论对象
        Integer restaurantId = review.getRestaurant().getId(); // 获取餐厅 ID
        
        reviewService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
        
        return "redirect:/vip/reviews/" + restaurantId + "/index"; // 使用餐厅 ID 跳转到评论列表页
    }
    
}
