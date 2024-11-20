package mvc.codejava.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.stream.Collectors;

public class VnPayUtil {

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA512");
            hmac.init(secretKeySpec);
            byte[] bytes = hmac.doFinal(data.getBytes("UTF-8"));
            return bytesToHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while generating HMAC SHA512", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String hashAllFields(Map<String, String> fields, String hashSecret) {
        // Bỏ qua các trường có giá trị null hoặc rỗng
        String data = fields.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey()) // Sắp xếp theo thứ tự từ điển
                .map(entry -> entry.getKey() + "=" + entry.getValue()) // Nối key=value
                .collect(Collectors.joining("&")); // Nối các cặp bằng '&'

        // Thêm hashSecret vào cuối chuỗi
        data += hashSecret;

        // Tạo mã băm SHA256
        return sha256(data);
    }

    // Hàm hỗ trợ tạo mã SHA256
    private static String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error generating SHA-256 hash", ex);
        }
    }
}
