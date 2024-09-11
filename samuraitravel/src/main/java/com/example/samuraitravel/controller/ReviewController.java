package com.example.samuraitravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.service.ReviewService;


@Controller
public class ReviewController {
	private final HouseRepository houseRepository; 
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;

    @Autowired
    public ReviewController(HouseRepository houseRepository, UserRepository userRepository,ReviewRepository reviewRepository, ReviewService reviewService) {
	    this.houseRepository = houseRepository;
	    this.userRepository = userRepository;
	    this.reviewRepository = reviewRepository;
	    this.reviewService = reviewService;
	}  

    @GetMapping("/reviews/list/{id}")
    public String listReviews(@PathVariable(name = "id") Integer id, Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC)Pageable pageable) {
    	User user =userRepository.getReferenceById(id);
        House house = houseRepository.getReferenceById(id);
        Page<Review> reviewPage = reviewRepository.findAll(pageable);

        model.addAttribute("house", house);
        model.addAttribute("user", user);
        model.addAttribute("reviewPage", reviewPage);

        return "reviews/list";
    } 
    

//    
//    @GetMapping("/edit")
//    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {        
//        Review review = reviewRepository.getReferenceById();
//        ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(), review.getRankStar(), review.getReview());
//        model.addAttribute("reviewEditForm", reviewEditForm);        
//        return "reviews/edit";
//    }    

//    @PostMapping("/update")
//        public String update(@ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
//            reviewService.update(reviewEditForm);
//            redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
//            
//            return "redirect:/reviews";
//        }    
    
}
