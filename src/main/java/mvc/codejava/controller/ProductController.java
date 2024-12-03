package mvc.codejava.controller;

import mvc.codejava.entity.*;
import mvc.codejava.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ProductDetailService productDetailService;
    @GetMapping("/home")
    public String showProductList(
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "brand", required = false) Long brandId,
            @RequestParam(value = "search", required = false) String search,
            Model model) {

        List<Product> products;

        if (categoryId != null && brandId != null) {
            products = productService.getProductsByCategoryAndBrand(categoryId, brandId);
        }
        else if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
        }
        else if (brandId != null) {
            products = productService.getProductsByBrand(brandId);
        }
        else if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
        }
        else {
            products = productService.getAllProducts();
        }

        // Lấy danh sách danh mục và thương hiệu
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands()); // Thêm vào để lấy danh sách thương hiệu
        model.addAttribute("products", products);
        model.addAttribute("search", search);

        return "home";
    }
    
    @GetMapping("/products/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        // Lấy thông tin sản phẩm từ database
        Product product = productService.findById(id);

        // Kiểm tra nếu sản phẩm không tồn tại
        if (product == null) {
            return "error"; // Hoặc chuyển tới trang lỗi
        }

        // Lấy thông tin chi tiết sản phẩm
        ProductDetail productDetail = productDetailService.getProductDetail(id);

        // Thêm dữ liệu vào model
        model.addAttribute("product", product);
        model.addAttribute("productDetail", productDetail);

        return "product-details"; // Tên của view (HTML) hiển thị chi tiết sản phẩm
    }

    @RequestMapping(value = "/products/add", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleAddProductForm(
            @ModelAttribute("product") Product product,
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "brand", required = false) Long brandId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "processor", required = false) String processor,
            @RequestParam(value = "memory", required = false) String memory,
            @RequestParam(value = "storage", required = false) String storage,
            @RequestParam(value = "screenSize", required = false) String screenSize,
            Model model, RedirectAttributes redirectAttributes) throws IOException {

        if (categoryId == null || brandId == null || imageFile == null || processor == null || memory == null || storage == null || screenSize == null) {
            // Hiển thị form thêm sản phẩm
            model.addAttribute("product", new Product());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            return "product-form";
        } else {
            // Tìm kiếm Category và Brand từ cơ sở dữ liệu
            Category category = categoryService.findById(categoryId);
            Brand brand = brandService.getBrandById(brandId);

            // Gán Category và Brand cho Product
            product.setCategory(category);
            product.setBrand(brand);

            // Lưu hình ảnh vào bảng Image và liên kết với Product
            if (!imageFile.isEmpty()) {
                // Upload ảnh lên Cloudinary và lấy URL
                String imageUrl = cloudinaryService.uploadImage(imageFile);

                // Tạo đối tượng Image và gán URL vào
                Image image = new Image();
                image.setUrl(imageUrl);

                // Lưu image vào database
                imageService.saveImage(image);

                // Gắn image vào sản phẩm
                product.setImage(image);
            }

            // Tạo ProductDetail và lưu vào database
            ProductDetail productDetail = new ProductDetail();
            productDetail.setProcessor(processor);
            productDetail.setMemory(memory);
            productDetail.setStorage(storage);
            productDetail.setScreenSize(screenSize);
            productDetailService.saveProductDetail(productDetail); // Lưu productDetail vào database
            product.setProductDetail(productDetail); // Gắn productDetail vào product

            // Lưu Product
            productService.saveProduct(product);

            // Chuyển hướng về trang chính với thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm thành công!");
            return "redirect:/home";
        }
    }

    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id); // Lấy sản phẩm từ database
        if (product == null) {
            return "redirect:/home";
        }
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "update-product"; // Trả về tên template
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute Product product, @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) throws IOException {
        if (!imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(imageFile);
        } else {
            Product existingProduct = productService.findById(product.getId());
        }
        productService.updateProduct(id, product);
        return "redirect:/home";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/home";
    }
}
