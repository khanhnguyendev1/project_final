package mvc.codejava.service;

import mvc.codejava.entity.Image;
import mvc.codejava.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    // Lưu hình ảnh vào cơ sở dữ liệu (giả sử lưu URL của hình ảnh)
    public void saveImage(Image image) {
        // Lưu đối tượng Image vào cơ sở dữ liệu
        imageRepository.save(image);
    }
}
