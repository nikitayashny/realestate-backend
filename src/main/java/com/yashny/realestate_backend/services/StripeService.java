package com.yashny.realestate_backend.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.yashny.realestate_backend.dto.PaymentRequest;
import com.yashny.realestate_backend.dto.StripeResponse;
import com.yashny.realestate_backend.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    public StripeResponse checkout(PaymentRequest paymentRequest, User user) {
        Stripe.apiKey = secretKey;
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(paymentRequest.getName())
                    .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("BYN")
                    .setUnitAmount(paymentRequest.getPrice() * 100)
                    .setProductData(productData)
                    .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                    .setQuantity(paymentRequest.getQuantity())
                    .setPriceData(priceData)
                    .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:3000/success")
                        .setCancelUrl("http://localhost:3000/cancel")
                        .addLineItem(lineItem)
                        .putMetadata("email", user.getEmail())
                        .putMetadata("name", paymentRequest.getName())
                        .putMetadata("quantity", paymentRequest.getQuantity().toString())
                        .build();

        Session session = null;

        try {
            session = Session.create(params);
        } catch (StripeException ex) {
            return StripeResponse.builder()
                    .status("ERROR")
                    .message("Failed to create payment session: " + ex.getMessage())
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();
        }

        return StripeResponse
                .builder()
                .status("SUCCESS")
                .message("Payment session created ")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

}

