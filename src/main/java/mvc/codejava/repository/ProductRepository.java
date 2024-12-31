package mvc.codejava.repository;

import mvc.codejava.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> searchByName(@Param("searchTerm") String searchTerm);

    Page<Product> findByCategoryIdAndBrandId(Long categoryId, Long brandId, Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByBrandId(Long brandId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findByCategoryIdAndPriceBetween(Long categoryId, Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findByBrandIdAndPriceBetween(Long brandId, Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findByCategoryIdAndBrandIdAndPriceBetween(Long categoryId, Long brandId, Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findByNameContainingOrDescriptionContainingAndPriceBetween(
            String name, String description, Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(String name, String brandName, Pageable pageable);
}



