package mvc.codejava.controller;

import mvc.codejava.entity.*;
import mvc.codejava.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String home(
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "brand", required = false) Long brandId,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "minPrice", defaultValue = "0") Double minPrice,
            @RequestParam(value = "maxPrice", defaultValue = "10000") Double maxPrice,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        int pageSize = 8; // Số sản phẩm mỗi trang
        Pageable pageable = PageRequest.of(page - 1, pageSize); // Spring bắt đầu từ 0

        Page<Product> productPage;

        if (categoryId == null && brandId == null && (search == null || search.isEmpty())) {
            productPage = productService.getProductsByPrice(minPrice, maxPrice, pageable);
        }
        else if (categoryId == null && brandId == null && (search == null || search.isEmpty())) {
            productPage = productService.getAllProducts(pageable);
        }
        else if (categoryId != null && brandId != null) {
            productPage = productService.getProductsByCategoryAndBrandAndPrice(categoryId, brandId, minPrice, maxPrice, pageable);
        }
        else if (categoryId != null) {
            productPage = productService.getProductsByCategoryAndPrice(categoryId, minPrice, maxPrice, pageable);
        }
        else if (brandId != null) {
            productPage = productService.getProductsByBrandAndPrice(brandId, minPrice, maxPrice, pageable);
        }
        else if (search != null && !search.isEmpty()) {
            productPage = productService.searchProductsByPrice(search, minPrice, maxPrice, pageable);
        }
        else {
            productPage = productService.getAllProducts(pageable);
        }

        // Thêm dữ liệu vào Model
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("products", productPage.getContent()); // Danh sách sản phẩm
        model.addAttribute("currentPage", productPage.getNumber() + 1); // Trang hiện tại
        model.addAttribute("totalPages", productPage.getTotalPages()); // Tổng số trang
        model.addAttribute("search", search);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "home";
    }


    @GetMapping("/products/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);

        if (product == null) {
            return "error";
        }

        ProductDetail productDetail = productDetailService.getProductDetail(id);

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
            model.addAttribute("product", new Product());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            return "product-form";
        } else {
            Category category = categoryService.findById(categoryId);
            Brand brand = brandService.getBrandById(brandId);
            product.setCategory(category);
            product.setBrand(brand);

            if (!imageFile.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(imageFile);

                Image image = new Image();
                image.setUrl(imageUrl);

                imageService.saveImage(image);

                product.setImage(image);
            }

            ProductDetail productDetail = new ProductDetail();
            productDetail.setProcessor(processor);
            productDetail.setMemory(memory);
            productDetail.setStorage(storage);
            productDetail.setScreenSize(screenSize);
            productDetailService.saveProductDetail(productDetail);
            product.setProductDetail(productDetail);

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
        List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        return "update-product";
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
