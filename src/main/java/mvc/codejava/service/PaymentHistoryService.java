package mvc.codejava.service;

import mvc.codejava.entity.PaymentHistory;
import mvc.codejava.repository.PaymentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentHistoryService {
    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    public List<PaymentHistory> getAllPaymentHistories() {
        return paymentHistoryRepository.findAll();
    }

    public PaymentHistory getPaymentHistoryById(Long id) {
        return paymentHistoryRepository.findById(id).orElse(null);
    }

    public PaymentHistory savePaymentHistory(PaymentHistory paymentHistory) {
        return paymentHistoryRepository.save(paymentHistory);
    }

    public void deletePaymentHistory(Long id) {
        paymentHistoryRepository.deleteById(id);
    }
}
