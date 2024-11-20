package mvc.codejava.service;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayService {

    public String createPaymentUrl(double amount, String returnUrl, String orderId) {
        // Cấu hình thông tin cơ bản
        String vnp_TmnCode = "6TQAY7Y1";
        String vnp_HashSecret = "58WBET5Z4GZF4MR6Z50698T8K0SRH42F";
        String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String vnp_ReturnUrl = "http://localhost:8080/checkout/success";

        try {
            // Khởi tạo các tham số cho URL
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_TmnCode", vnp_TmnCode);
            vnpParams.put("vnp_OrderInfo", "Order Information");
            vnpParams.put("vnp_Amount", String.valueOf(amount * 100)); // VNPAY yêu cầu số tiền phải nhân với 100
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_OrderId", orderId);
            vnpParams.put("vnp_Currency", "VND");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_TxnRef", orderId); // Mã tham chiếu giao dịch

            // Thêm các tham số khác nếu cần
            vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

            // Hash các tham số và tạo URL thanh toán
            String secureHash = VnPayUtil.hashAllFields(vnpParams, vnp_HashSecret);
            vnpParams.put("vnp_SecureHash", secureHash);

            // Tạo URL thanh toán VNPAY
            StringBuilder vnpUrl = new StringBuilder("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?");
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                vnpUrl.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            return vnpUrl.toString().substring(0, vnpUrl.length() - 1); // Loại bỏ ký tự "&" cuối cùng

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create VNPAY payment URL.");
        }
    }

    public boolean validatePaymentResponse(Map<String, String> responseParams) {
        // Lấy `vnp_SecureHash` từ phản hồi
        String vnpSecureHash = responseParams.get("vnp_SecureHash");
        if (vnpSecureHash == null || vnpSecureHash.isEmpty()) {
            return false;
        }

        // Loại bỏ `vnp_SecureHash` khỏi danh sách tham số để tính chữ ký
        Map<String, String> filteredParams = new HashMap<>(responseParams);
        filteredParams.remove("vnp_SecureHash");

        // Sắp xếp các tham số theo thứ tự key tăng dần
        List<String> sortedKeys = new ArrayList<>(filteredParams.keySet());
        Collections.sort(sortedKeys);

        // Tạo chuỗi dữ liệu để hash
        StringBuilder dataToHash = new StringBuilder();
        for (String key : sortedKeys) {
            String value = filteredParams.get(key);
            if (value != null && !value.isEmpty()) {
                dataToHash.append(key).append("=").append(value).append("&");
            }
        }
        // Loại bỏ ký tự `&` cuối cùng
        if (dataToHash.length() > 0) {
            dataToHash.setLength(dataToHash.length() - 1);
        }

        // Hash dữ liệu với secret key
        String hashSecret = "YOUR_HASH_SECRET"; // Thay bằng secret key của bạn
        String generatedHash = VnPayUtil.hmacSHA512(hashSecret, dataToHash.toString());

        // So sánh chữ ký được tạo và chữ ký từ phản hồi
        return generatedHash.equals(vnpSecureHash);
    }


}
