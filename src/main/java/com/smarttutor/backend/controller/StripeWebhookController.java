package com.smarttutor.backend.controller;

import com.smarttutor.backend.model.Payment;
import com.smarttutor.backend.repository.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    private final PaymentRepository paymentRepo;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public StripeWebhookController(PaymentRepository paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    @PostMapping("/webhook")
    public String handleStripeEvent(@RequestBody String payload,
                                    @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

                if (session != null) {
                    String sessionId = session.getId();
                    Optional<Payment> paymentOpt = paymentRepo.findAll().stream()
                            .filter(p -> sessionId.equals(p.getTransactionId()))
                            .findFirst();

                    paymentOpt.ifPresent(payment -> {
                        payment.setStatus("SUCCESS");
                        paymentRepo.save(payment);
                    });
                }
            }

            return "Webhook handled";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
