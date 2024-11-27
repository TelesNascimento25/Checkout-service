package com.qikserve.checkout.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

import com.qikserve.checkout.model.Basket.Views.Create;
import com.qikserve.checkout.model.Basket.Views.Created;
import com.qikserve.checkout.model.Basket.Views.Read;

@Entity
@Table(name = "baskets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@With
@JsonInclude(Include.NON_NULL)
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Read.class, Created.class})
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JsonView({Read.class, Created.class})
    private BasketStatus status;

    @JsonView({Read.class, Created.class})
    private BigDecimal total;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference()
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    @JsonView(Read.class)
    private List<BasketItem> basketItems;

    @JsonProperty
    @JsonView({Read.class, Created.class})
    public int getItemsCount() {
        return CollectionUtils.size(basketItems);
    }

    public static class Views {
        public static class Create {}

        public static class Created extends BasketItem.Views.Create {}

        public static class Read extends BasketItem.Views.Read {}

    }

}
