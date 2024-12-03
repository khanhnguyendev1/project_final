package mvc.codejava.service;

import mvc.codejava.entity.Brand;
import mvc.codejava.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    // Lấy tất cả thương hiệu
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    // Lấy thương hiệu theo id
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElse(null);
    }

    // Lưu thương hiệu mới hoặc cập nhật thương hiệu
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }
}
