package com.qikserve.checkout.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.qikserve.checkout.model.Basket.Views.Created;
import com.qikserve.checkout.model.Basket.Views.Read;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

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
