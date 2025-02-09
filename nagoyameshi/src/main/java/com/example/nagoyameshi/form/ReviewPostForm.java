package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor

public class ReviewPostForm {
	
    private Integer restaurantId;
    private Integer userId;
	
	@NotNull(message = "評価を選択してください。")
    private Integer rankStar;
 
    @NotBlank(message = "レビュー内容を入れてください。")
    private String review;

}
