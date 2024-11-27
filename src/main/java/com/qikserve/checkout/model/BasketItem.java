package com.qikserve.checkout.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;

import com.qikserve.checkout.model.BasketItem.Views.Created;
import com.qikserve.checkout.model.BasketItem.Views.Create;
import com.qikserve.checkout.model.BasketItem.Views.Read;
import com.qikserve.checkout.model.BasketItem.Views.Update;


@Entity
@Table(name = "basket_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@With
@EqualsAndHashCode
@JsonInclude(Include.NON_NULL)
public class BasketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Created.class, Read.class})
    private Long id;

    @Column(name = "basket_id", nullable = false)
    @JsonView({Read.class, Create.class})
    private Long basketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basket_id", insertable = false, updatable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonView({Read.class, Created.class})
    @JsonInclude(Include.NON_DEFAULT)
    private Basket basket;

    @Column(name = "product_id", nullable = false)
    @JsonView({Create.class, Read.class})
    private String productId;

    @Column(nullable = false)
    @JsonView({Create.class, Read.class, Update.class})
    private Integer quantity;


    public static class Views {
        public static class Create {}

        public static class Created extends Create {}

        public static class Read {}

        public static class Update {}

    }

}