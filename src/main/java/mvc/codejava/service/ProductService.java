package mvc.codejava.service;

import mvc.codejava.entity.Product;
import mvc.codejava.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;


    public void saveProduct(Product product) {
        // Lưu sản phẩm vào cơ sở dữ liệu
        productRepository.save(product);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void updateProduct(Long id, Product updatedProduct) {
        Product product = findById(id);
        if (product != null) {
            product.setName(updatedProduct.getName());
            product.setPrice(updatedProduct.getPrice());
            product.setDescription(updatedProduct.getDescription());
            product.setStock(updatedProduct.getStock());
            product.setBrand(updatedProduct.getBrand());
            product.setCategory(updatedProduct.getCategory());
            productRepository.save(product);
        }
    }

    // Xóa sản phẩm
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Page<Product> getProductsByCategoryAndBrand(Long categoryId, Long brandId, Pageable pageable) {
        return productRepository.findByCategoryIdAndBrandId(categoryId, brandId, pageable);
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Page<Product> getProductsByBrand(Long brandId, Pageable pageable) {
        return productRepository.findByBrandId(brandId, pageable);
    }

    public Page<Product> searchProducts(String search, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(search, pageable);
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}
