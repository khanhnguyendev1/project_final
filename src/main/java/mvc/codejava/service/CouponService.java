package mvc.codejava.service;

import mvc.codejava.entity.Coupon;
import mvc.codejava.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {
    @Autowired
    private CouponRepository couponRepository;

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id).orElse(null);
    }

    public Coupon saveCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    public Coupon findByCode(String code) {
        return couponRepository.findByCode(code);
    }
}
