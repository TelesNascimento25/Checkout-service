package com.qikserve.checkout.service.promotion;

import com.qikserve.checkout.model.dto.BuyXGetYFree;
import com.qikserve.checkout.model.dto.Promotion.PromotionType;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class BuyXGetYFreeStrategy implements PromotionStrategy {
    private final BuyXGetYFree promotion;

    @Override
    public BigDecimal computeFinalPriceInPence(int quantity, int priceInPence) {
        var diff = quantity - promotion.getRequiredQty();
        return BigDecimal.valueOf(priceInPence).multiply(BigDecimal.valueOf(Integer.max(0, diff)));
    }

    @Override
    public boolean isApplicable(int quantity, int priceInPence) {
        return quantity >= promotion.getRequiredQty();
    }

    @Override
    public PromotionType getPromotionType() {
        return PromotionType.BUY_X_GET_Y_FREE;
    }

}
