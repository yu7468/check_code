package com.example.nagoyameshi.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.event.TimeSlotUtils;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReservationService;

@Controller
@RequestMapping("/vip/reservations")
public class ReservationController {
    private final ReservationRepository reservationRepository;    
    private final RestaurantRepository restaurantRepository;
    private final ReservationService reservationService; 

    public ReservationController(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository,ReservationService reservationService) {        
        this.reservationRepository = reservationRepository;   
        this.restaurantRepository = restaurantRepository;
        this.reservationService = reservationService;
    }

    @GetMapping("/index")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
        User user = userDetailsImpl.getUser();
        Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        model.addAttribute("reservationPage", reservationPage);         
        
        return "vip/reservations/index"; 
    }

    @GetMapping("/{restaurantId}/input")
    public String input(
        @PathVariable("restaurantId") Integer restaurantId, 
        @ModelAttribute ReservationInputForm reservationInputForm,
        BindingResult bindingResult,
        Model model
    ) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        String closedDays = restaurant.getClosedDay();
        System.out.println("Closed Days from DB: " + closedDays);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("closedDays", String.join(",", closedDays));
        model.addAttribute("reservationInputForm", reservationInputForm == null ? new ReservationInputForm() : reservationInputForm);
        
        return "vip/reservations/input";
    }



    @GetMapping("/{restaurantId}/time-slots")
    @ResponseBody
    public ResponseEntity<Map<String, List<String>>> getTimeSlots(
        @PathVariable Integer restaurantId, 
        @RequestParam(required = false) String checkDate) {
        if (checkDate == null || checkDate.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", List.of("checkDate is required")));
        }

        try {
            LocalDate date = LocalDate.parse(checkDate);
            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            List<String> timeSlots = TimeSlotUtils.generateTimeSlots(
                restaurant.getOpenTime(), restaurant.getCloseTime(), 30);

            List<String> finishTimeSlots = timeSlots.stream().skip(1).collect(Collectors.toList());

            Map<String, List<String>> response = new HashMap<>();
            response.put("startTimeSlots", timeSlots);
            response.put("finishTimeSlots", finishTimeSlots);

            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", List.of("Invalid date format")));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", List.of("Internal server error")));
        }
    }


    @PostMapping("/{restaurantId}/submit")
    public String submit(
    		
        @PathVariable("restaurantId") Integer restaurantId,
        @ModelAttribute ReservationInputForm reservationInputForm,
        @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        reservationInputForm.setRestaurantId(restaurantId);
        reservationInputForm.setUserId(userDetailsImpl.getUser().getId()); 

        reservationService.submit(reservationInputForm);
        return "redirect:/vip/reservations/index?reserved";
    }

}