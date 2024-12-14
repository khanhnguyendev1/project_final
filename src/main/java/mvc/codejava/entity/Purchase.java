package mvc.codejava.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;
    private String status;

    @ManyToOne
    @JoinColumn(name = "payment_history_id")
    private PaymentHistory paymentHistory;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.PERSIST)
    private List<PurchaseItem> purchaseItems;

    @ManyToOne
    private User user;

    private String name;
    private String address;
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<PurchaseItem> getPurchaseItems() {
        return purchaseItems;
    }

    public void setPurchaseItems(List<PurchaseItem> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public PaymentHistory getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(PaymentHistory paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
