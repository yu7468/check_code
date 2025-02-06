package com.example.nagoyameshi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.form.UpgradeForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {
	@Value("${stripe.api-key}")
    private String stripeApiKey;
   
   

    public boolean checkPaymentCompletion(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return "paid".equals(session.getPaymentStatus());
        } catch (StripeException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String createStripeSession(String userEmail, UpgradeForm upgradeForm, HttpServletRequest request) {
    	Stripe.apiKey = stripeApiKey;
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");

        SessionCreateParams params = SessionCreateParams.builder()
            .setCustomerEmail(userEmail)
            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(baseUrl + "/general/user/upgrade/success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(baseUrl + "/general/user/upgrade/cancel")
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("jpy")
                            .setUnitAmount(upgradeForm.getAmount())
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("VIP Membership Upgrade")
                                    .build())
                            .build())
                    .setQuantity(1L)
                    .build())
            .build();

        try {
            Session session = Session.create(params);
            return session.getId();
        } catch (StripeException e) {
            e.printStackTrace();
            return null;
        }
    }
}