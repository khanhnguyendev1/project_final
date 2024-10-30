package mvc.codejava.controller;

import mvc.codejava.entity.Category;
import mvc.codejava.entity.Product;
import mvc.codejava.service.CategoryService;
import mvc.codejava.service.CloudinaryService;
import mvc.codejava.service.ProductService;
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

    @GetMapping("/home")
    public String showProductList(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "home";
    }

    @GetMapping("/products/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product-details";
    }

    @RequestMapping(value = "/products/add", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleAddProductForm(@ModelAttribute("product") Product product,
                                       @RequestParam(value = "category", required = false) Long categoryId,
                                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                       Model model, RedirectAttributes redirectAttributes) throws IOException {
        if (categoryId == null && imageFile == null) {
            model.addAttribute("product", new Product());
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return "product-form";
        } else {
            Category category = categoryService.findById(categoryId);
            product.setCategory(category);
            if (!imageFile.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                product.setImageUrl(imageUrl);
            }
            productService.saveProduct(product);
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
            product.setImageUrl(imageUrl);
        } else {
            Product existingProduct = productService.findById(product.getId());
            product.setImageUrl(existingProduct.getImageUrl());
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
