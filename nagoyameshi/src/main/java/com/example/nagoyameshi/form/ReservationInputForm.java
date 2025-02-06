package com.example.nagoyameshi.form;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ReservationInputForm {

    private Integer userId;

    private Integer restaurantId;
    
    private LocalDate checkDate;
    
    private String startTime;

    private String finishTime;
    
    private Integer people;
}

