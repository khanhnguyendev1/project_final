package mvc.codejava.service;

import mvc.codejava.entity.ProductDetail;
import mvc.codejava.repository.ProductDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductDetailService {

    @Autowired
    private ProductDetailRepository productDetailRepository;

    public ProductDetail saveProductDetail(ProductDetail productDetail) {
        return productDetailRepository.save(productDetail);
    }

    public ProductDetail findByProductId(Long productId) {
        return productDetailRepository.findByProductId(productId);
    }

    public ProductDetail getProductDetail(Long productId) {
        return productDetailRepository.findByProductId(productId);
    }
}
