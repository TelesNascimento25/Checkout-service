package com.qikserve.checkout.repository;

import com.qikserve.checkout.model.Basket;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {

    @EntityGraph(attributePaths = {"basketItems.productId", "basketItems.quantity"})
    @Query("from Basket b left join fetch b.basketItems bi where b.id = :id")
    Optional<Basket> fetchCheckoutItemsById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("delete from BasketItem bi where bi.basketId = :basketId")
    int clearBasket(@Param("basketId") Long basketId);
}
