package com.example.nagoyameshi.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestaurantEditForm {
	@NotNull
    private Integer id;
	
	@NotBlank(message = "店名を入力してください。")
    private String name;
        
    private MultipartFile imageFile;
    
    @NotBlank(message = "説明を入力してください。")
    private String description;  
    
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
