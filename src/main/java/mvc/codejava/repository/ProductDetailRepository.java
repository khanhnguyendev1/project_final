package mvc.codejava.repository;

import mvc.codejava.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    ProductDetail findByProductId(Long productId);
}
