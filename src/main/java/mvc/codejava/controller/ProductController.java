package mvc.codejava.controller;

import mvc.codejava.entity.Category;
import mvc.codejava.entity.Product;
import mvc.codejava.service.CategoryService;
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

    @GetMapping("/home")
    public String showProductList(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "home";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());

        // Lấy danh sách các danh mục sản phẩm từ cơ sở dữ liệu
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "product-form";
    }

    @PostMapping("/products/save")
    public String addProduct(@ModelAttribute("product") Product product, @RequestParam("category") Long categoryId, RedirectAttributes redirectAttributes) {
        // Gán danh mục vào sản phẩm
        Category category = categoryService.findById(categoryId);
        product.setCategory(category);
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm thành công!");
        return "redirect:/home";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id); // Lấy sản phẩm từ database
        if (product == null) {
            return "redirect:/products"; // Chuyển hướng nếu không tìm thấy sản phẩm
        }
        List<Category> categories = categoryService.getAllCategories(); // Lấy tất cả danh mục
        model.addAttribute("product", product); // Thêm sản phẩm vào model
        model.addAttribute("categories", categories); // Thêm danh sách danh mục vào model
        return "update-product"; // Trả về tên template
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute Product product) {
        productService.updateProduct(id, product); // Cập nhật sản phẩm
        return "redirect:/home"; // Chuyển hướng đến danh sách sản phẩm
    }


    // Xóa sản phẩm
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/home";
    }
}
