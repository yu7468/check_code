package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class RestaurantService {
	
	private final RestaurantRepository restaurantRepository;
		public RestaurantService(RestaurantRepository restaurantRepository) {
			this.restaurantRepository = restaurantRepository;
		}
		
		@Transactional
		public void create(RestaurantRegisterForm restaurantRegisterForm) {
			Restaurant restaurant = new Restaurant();        
	         MultipartFile imageFile = restaurantRegisterForm.getImageFile();
	         
	         if (!imageFile.isEmpty()) {
	             String imageName = imageFile.getOriginalFilename(); 
	             String hashedImageName = generateNewFileName(imageName);
	             Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
	             copyImageFile(imageFile, filePath);
	             restaurant.setImageName(hashedImageName);
	         }
	         
	         restaurant.setName(restaurantRegisterForm.getName());                
	         restaurant.setDescription(restaurantRegisterForm.getDescription());
	         restaurant.setMinPrice(restaurantRegisterForm.getMinPrice());
	         restaurant.setMaxPrice(restaurantRegisterForm.getMaxPrice());
	         restaurant.setOpen(restaurantRegisterForm.getOpen());
	         restaurant.setClose(restaurantRegisterForm.getClose());
	         restaurant.setClosedDay(restaurantRegisterForm.getClosedDay());
	         restaurant.setPostalCode(restaurantRegisterForm.getPostalCode());
	         restaurant.setAddress(restaurantRegisterForm.getAddress());
	         restaurant.setPhoneNumber(restaurantRegisterForm.getPhoneNumber());
	         restaurant.setCategories(restaurantRegisterForm.getCategoryIds());      
	                     
	         restaurantRepository.save(restaurant);
	     }  
		
		@Transactional
	     public void update(RestaurantEditForm restaurantEditForm) {
	         Restaurant restaurant = restaurantRepository.getReferenceById(restaurantEditForm.getId());
	         MultipartFile imageFile = restaurantEditForm.getImageFile();
	         
	         if (!imageFile.isEmpty()) {
	             String imageName = imageFile.getOriginalFilename(); 
	             String hashedImageName = generateNewFileName(imageName);
	             Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
	             copyImageFile(imageFile, filePath);
	             restaurant.setImageName(hashedImageName);
	         }
	         
	         restaurant.setName(restaurantEditForm.getName());                
	         restaurant.setDescription(restaurantEditForm.getDescription());
	         restaurant.setMinPrice(restaurantEditForm.getMinPrice());
	         restaurant.setMaxPrice(restaurantEditForm.getMaxPrice());
	         restaurant.setOpen(restaurantEditForm.getOpen());
	         restaurant.setClose(restaurantEditForm.getClose());
	         restaurant.setClosedDay(restaurantEditForm.getClosedDay());
	         restaurant.setPostalCode(restaurantEditForm.getPostalCode());
	         restaurant.setAddress(restaurantEditForm.getAddress());
	         restaurant.setPhoneNumber(restaurantEditForm.getPhoneNumber());
      
	         restaurantRepository.save(restaurant);
	     }
		
		public String generateNewFileName(String fileName) {
	         String[] fileNames = fileName.split("\\.");                
	         for (int i = 0; i < fileNames.length - 1; i++) {
	             fileNames[i] = UUID.randomUUID().toString();            
	         }
	         String hashedFileName = String.join(".", fileNames);
	         return hashedFileName;
	     } 
		
		public void copyImageFile(MultipartFile imageFile, Path filePath) {           
	         try {
	             Files.copy(imageFile.getInputStream(), filePath);
	         } catch (IOException e) {
	             e.printStackTrace();
	         }          
	     } 

}
