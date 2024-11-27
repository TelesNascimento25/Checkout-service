package com.qikserve.checkout.service.promotion;

import com.qikserve.checkout.model.dto.Promotion.PromotionType;

import java.math.BigDecimal;

public interface PromotionStrategy {
    BigDecimal computeFinalPriceInPence(int quantity, int priceInPence);
    boolean isApplicable(int quantity, int priceInPence);

    default BigDecimal applyPromotion(int quantity, int priceInPence) {
        if (isApplicable(quantity, priceInPence)) {
            return computeFinalPriceInPence(quantity, priceInPence);
        }
        return BigDecimal.valueOf(priceInPence).multiply(BigDecimal.valueOf(quantity));
    }

    PromotionType getPromotionType();
}
