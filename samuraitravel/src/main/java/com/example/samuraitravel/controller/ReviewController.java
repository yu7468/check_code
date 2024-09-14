package com.example.samuraitravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.service.ReviewService;


@Controller
public class ReviewController {
	private final HouseRepository houseRepository; 
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;

    @Autowired
    public ReviewController(HouseRepository houseRepository, UserRepository userRepository,ReviewRepository reviewRepository, ReviewService reviewService) {
	    this.houseRepository = houseRepository;
	    this.reviewRepository = reviewRepository;
	    this.reviewService = reviewService;
	}  

    @GetMapping("/reviews/list/{houseId}")
    public String list(@PathVariable(name = "houseId") int houseId, Model model, @PageableDefault(page = 0, size = 10, sort = "houseId", direction = Direction.ASC)Pageable pageable) {
//    	User user =userRepository.getReferenceById(id);
        House house = houseRepository.getReferenceById(houseId);
        Page<Review> reviewPage = reviewRepository.findByHouse(house,pageable);

        model.addAttribute("house", house);
        model.addAttribute("reviewPage", reviewPage);

        return "reviews/list";
    } 
    

    
    @GetMapping("/reviews/edit/{reviewId}")
    public String edit(@PathVariable(name = "reviewId") Integer reviewId, Model model) {        
        Review review = reviewRepository.getReferenceById(reviewId);
        House house = houseRepository.getReferenceById(review.getHouse().getId());
        ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(), review.getRankStar(), review.getReview());
        
        model.addAttribute("house", house);
        model.addAttribute("reviewEditForm", reviewEditForm);        
        return "reviews/edit";
    }    

    @PostMapping("/reviews/update")
        public String update(@ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    	if (bindingResult.hasErrors()) {
            return "reviews/edit";
        }
    	reviewService.update(reviewEditForm);
            redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
            
            return "redirect:/houses/show";
        }
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {        
        reviewRepository.deleteById(id);
                
        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
        
        return "redirect:/reviews/list";
    }
    
}
