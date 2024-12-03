package mvc.codejava.service;

import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.charset.StandardCharsets;

@Service
public class VnPayService {

    public String createPaymentUrl(double amount, String returnUrl, String orderId) {

        String vnp_TmnCode = "6TQAY7Y1";
        String vnp_HashSecret = "58WBET5Z4GZF4MR6Z50698T8K0SRH42F";
        String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String vnp_ReturnUrl = "http://localhost:8080/checkout/success";

        try {
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_TmnCode", vnp_TmnCode);
            vnpParams.put("vnp_OrderInfo", "Order Information");
            vnpParams.put("vnp_Amount", String.valueOf(amount * 100));
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_OrderId", orderId);
            vnpParams.put("vnp_Currency", "VND");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_TxnRef", orderId);
            vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));


            String secureHash = VnPayUtil.hashAllFields(vnpParams, vnp_HashSecret);
            vnpParams.put("vnp_SecureHash", secureHash);


            StringBuilder vnpUrl = new StringBuilder(vnp_PayUrl + "?");
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                vnpUrl.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .append("&");
            }

            return vnpUrl.toString().substring(0, vnpUrl.length() - 1);

        } catch (Exception e) {
            System.err.println("Error while creating VNPAY payment URL: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create VNPAY payment URL: " + e.getMessage());
        }
    }

    public boolean validatePaymentResponse(Map<String, String> responseParams) {
        String vnpSecureHash = responseParams.get("vnp_SecureHash");
        if (vnpSecureHash == null || vnpSecureHash.isEmpty()) {
            return false;
        }


        Map<String, String> filteredParams = new HashMap<>(responseParams);
        filteredParams.remove("vnp_SecureHash");


        List<String> sortedKeys = new ArrayList<>(filteredParams.keySet());
        Collections.sort(sortedKeys);


        StringBuilder dataToHash = new StringBuilder();
        for (String key : sortedKeys) {
            String value = filteredParams.get(key);
            if (value != null && !value.isEmpty()) {
                dataToHash.append(key).append("=").append(value).append("&");
            }
        }

        if (dataToHash.length() > 0) {
            dataToHash.setLength(dataToHash.length() - 1);
        }


        String hashSecret = "58WBET5Z4GZF4MR6Z50698T8K0SRH42F"; // Thay bằng secret key của bạn
        String generatedHash = VnPayUtil.hmacSHA512(hashSecret, dataToHash.toString());


        return generatedHash.equals(vnpSecureHash);
    }


}
