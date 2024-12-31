package mvc.codejava.service;

import mvc.codejava.entity.Product;
import mvc.codejava.entity.PurchaseItem;
import mvc.codejava.repository.ProductRepository;
import mvc.codejava.repository.PurchaseItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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


    @Autowired
    private PurchaseItemRepository purchaseItemRepository;

    public void saveProduct(Product product) {
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

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        List<PurchaseItem> purchaseItems = purchaseItemRepository.findByProduct(product);
        for (PurchaseItem item : purchaseItems) {
            item.setProduct(null);
            purchaseItemRepository.save(item);
        }

        productRepository.delete(product);;
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

    public Page<Product> getProductsByPrice(Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    public Page<Product> getProductsByCategoryAndPrice(Long categoryId, Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findByCategoryIdAndPriceBetween(categoryId, minPrice, maxPrice, pageable);
    }

    public Page<Product> getProductsByBrandAndPrice(Long brandId, Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findByBrandIdAndPriceBetween(brandId, minPrice, maxPrice, pageable);
    }

    public Page<Product> getProductsByCategoryAndBrandAndPrice(Long categoryId, Long brandId, Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findByCategoryIdAndBrandIdAndPriceBetween(categoryId, brandId, minPrice, maxPrice, pageable);
    }

    public Page<Product> searchProductsByPrice(String search, Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findByNameContainingOrDescriptionContainingAndPriceBetween(search, search, minPrice, maxPrice, pageable);
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> getAllProductss() {
        return productRepository.findAll();
    }

    public Page<Product> searchProducts(String keyword, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return productRepository.findByNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(keyword, keyword, pageable);
    }

    public Page<Product> getAllProducts(int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return productRepository.findAll(pageable);
    }
}
