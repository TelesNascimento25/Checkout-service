package com.qikserve.checkout.service.factory;

import com.qikserve.checkout.model.dto.BuyXGetYFree;
import com.qikserve.checkout.model.dto.FlatPercent;
import com.qikserve.checkout.model.dto.Product;
import com.qikserve.checkout.model.dto.Promotion;
import com.qikserve.checkout.model.dto.QtyBasedPriceOverride;
import com.qikserve.checkout.service.promotion.BuyXGetYFreeStrategy;
import com.qikserve.checkout.service.promotion.FlatPercentStrategy;
import com.qikserve.checkout.service.promotion.PromotionStrategy;
import com.qikserve.checkout.service.promotion.QtyBasedPriceOverrideStrategy;
import com.qikserve.checkout.util.PenceUtils;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;

public class PromotionStrategyFactory {

    public static PromotionStrategy getPromotionStrategy(Promotion promotion) {
        return switch (promotion.getType()) {
            case BUY_X_GET_Y_FREE -> new BuyXGetYFreeStrategy((BuyXGetYFree) promotion);
            case FLAT_PERCENT -> new FlatPercentStrategy((FlatPercent) promotion);
            case QTY_BASED_PRICE_OVERRIDE -> new QtyBasedPriceOverrideStrategy((QtyBasedPriceOverride) promotion);
        };
    }

    private static BigDecimal applyPromotion(Operand operand) {
        return getPromotionStrategy(operand.promotion())
                .applyPromotion(
                        operand.quantity(),
                        operand.priceInPence());
    }

    public static BigDecimal applyPromotions(Product product, int quantity) {
        var promotions = product.getPromotions();
        if (CollectionUtils.isEmpty(promotions)) {
            return PenceUtils.computeTotal(quantity, product.getPrice());
        }
        Operand.OperandBuilder builder = Operand.builder().quantity(quantity).priceInPence(product.getPrice());

        return promotions.stream()
            .map(builder::build)
            .map(PromotionStrategyFactory::applyPromotion)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Builder(buildMethodName = "_build")
    private record Operand(Promotion promotion, int quantity, int priceInPence) {
        public static class OperandBuilder {
            public Operand build(final Promotion promotion) {
                return this.promotion(promotion)._build();
            }
        }
    }
}
