package mvc.codejava.service;

import mvc.codejava.entity.Product;
import mvc.codejava.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Product> getAllProducts() {
        return productRepository.findAll(); // Lấy tất cả sản phẩm từ cơ sở dữ liệu
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
}
