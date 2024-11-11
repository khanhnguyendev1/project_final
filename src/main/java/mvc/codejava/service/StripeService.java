package mvc.codejava.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.billingportal.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;


    public StripeService() {
        Stripe.apiKey = stripeSecretKey;
    }

    public String createPaymentIntent(double amount) throws Exception {
        if (Stripe.apiKey == null || Stripe.apiKey.isEmpty()) {
            Stripe.apiKey = stripeSecretKey;
        }

        if (amount < 0.50) {
            throw new IllegalArgumentException("Amount must be at least 0.50 USD.");
        }


        long amountInCents = (long) (amount * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100)) // Stripe sử dụng đơn vị cents
                .setCurrency("usd")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }


}
