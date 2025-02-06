package com.example.nagoyameshi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	
    Page<Restaurant> findByNameContainingOrAddressContainingOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);
    Page<Restaurant> findByNameContainingOrAddressContainingOrderByMaxPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);
    Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Restaurant> findAllByOrderByMaxPriceAsc(Pageable pageable);
    Page<Restaurant> findByMaxPriceLessThanEqualOrderByCreatedAtDesc(Integer maxPrice, Pageable pageable);
    Page<Restaurant> findByMaxPriceLessThanEqualOrderByMaxPriceAsc(Integer maxPrice, Pageable pageable);
    
    public List<Restaurant> findTop10ByOrderByCreatedAtDesc();
    
    public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);
	
    @Query("SELECT r FROM Restaurant r JOIN r.categories c WHERE c.name LIKE %:categoryName% ORDER BY r.maxPrice ASC")
    Page<Restaurant> findByCategoryNameLikeOrderByMaxPriceAsc(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT r FROM Restaurant r JOIN r.categories c WHERE c.name LIKE %:categoryName% ORDER BY r.createdAt DESC")
    Page<Restaurant> findByCategoryNameLikeOrderByCreatedAtDesc(@Param("categoryName") String categoryName, Pageable pageable);
    
	@Query("SELECT r FROM Restaurant r JOIN r.categories c WHERE c.name LIKE %:categoryName%")
    Page<Restaurant> findByCategoryNameLike(@Param("categoryName") String categoryName, Pageable pageable);
    
	@Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.categories WHERE r.id = :id")
	Optional<Restaurant> findByIdWithCategories(@Param("id") Integer id);
    @EntityGraph(attributePaths = {"categories"})
    Optional<Restaurant> findById(Integer id);
	

}
