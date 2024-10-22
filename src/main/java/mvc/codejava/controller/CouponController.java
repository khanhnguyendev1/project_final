package mvc.codejava.controller;

import mvc.codejava.entity.Coupon;
import mvc.codejava.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public String listCoupons(Model model) {
        model.addAttribute("coupons", couponService.getAllCoupons());
        return "coupon-list";
    }

    @GetMapping("/add")
    public String showAddCouponForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        return "coupon-form";
    }

    @PostMapping("/save")
    public String saveCoupon(@ModelAttribute("coupon") Coupon coupon) {
        couponService.saveCoupon(coupon);
        return "redirect:/coupons";
    }

    @GetMapping("/edit/{id}")
    public String showEditCouponForm(@PathVariable Long id, Model model) {
        Coupon coupon = couponService.getCouponById(id);
        model.addAttribute("coupon", coupon);
        return "coupon-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return "redirect:/coupons";
    }
}
