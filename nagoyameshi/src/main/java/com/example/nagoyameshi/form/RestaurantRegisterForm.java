package com.example.nagoyameshi.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestaurantRegisterForm {
	@NotBlank(message = "店名を入力してください。")
    private String name;
        
    private MultipartFile imageFile;
    
    @NotBlank(message = "説明を入力してください。")
    private String description;   
    
    @NotEmpty(message = "カテゴリーを選択してください。")
    private List<Category> categoryIds;
    
    @NotNull(message = "最低価格を入力してください。")
    @Min(value = 1, message = "料金は1円以上に設定してください。")
    private Integer minPrice;  
    
    @NotNull(message = "最高価格を入力してください。")
    @Min(value = 1, message = "料金は1円以上に設定してください。")
    private Integer maxPrice; 
    
    @NotNull(message = "営業時間を選択してください。")
    private String open;
    
    @NotNull(message = "営業時間を選択してください。")
    private String close;
    
    @NotBlank(message = "定休日を入力してください。")
    private String closedDay;
    
    @NotBlank(message = "郵便番号を入力してください。")
    private String postalCode;
    
    @NotBlank(message = "住所を入力してください。")
    private String address;
    
    @NotBlank(message = "電話番号を入力してください。")
    private String phoneNumber;

}
