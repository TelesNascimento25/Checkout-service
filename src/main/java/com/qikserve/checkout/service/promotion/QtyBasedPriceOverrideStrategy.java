package com.qikserve.checkout.service.promotion;

import com.qikserve.checkout.model.dto.Promotion.PromotionType;
import com.qikserve.checkout.model.dto.QtyBasedPriceOverride;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class QtyBasedPriceOverrideStrategy implements PromotionStrategy {
    private final QtyBasedPriceOverride promotion;

    @Override
    public BigDecimal computeFinalPriceInPence(int quantity, int priceInPence) {
        var fullPriceQty = BigDecimal.valueOf(quantity - promotion.getRequiredQty());
        var fullPriceTerm = BigDecimal.valueOf(priceInPence).multiply(fullPriceQty);
        var discountedTerm = BigDecimal.valueOf(promotion.getPrice());
        return fullPriceTerm.add(discountedTerm);
    }

    @Override
    public boolean isApplicable(int quantity, int priceInPence) {
        return quantity >= promotion.getRequiredQty();
    }

    @Override
    public PromotionType getPromotionType() {
        return PromotionType.QTY_BASED_PRICE_OVERRIDE;
    }
}
