package com.qikserve.checkout.repository;

import com.qikserve.checkout.model.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
    
}
