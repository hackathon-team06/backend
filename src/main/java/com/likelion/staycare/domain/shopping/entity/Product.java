package com.likelion.staycare.domain.shopping.entity;

import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.global.common.BaseTimeEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "modifiedAt", column = @Column(name = "modified_at"))
})
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 100)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductCategory category;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "product_skin_types",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "skin_type", nullable = false, length = 20)
    private Set<SkinType> skinTypes = new HashSet<>();

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "purchase_url", nullable = false, length = 500)
    private String purchaseUrl;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "original_price")
    private Integer originalPrice;

    @Column(name = "discount_rate")
    private Integer discountRate;

    @Column(name = "price_updated_at", nullable = false)
    private LocalDateTime priceUpdatedAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Builder
    public Product(
            String name,
            String brand,
            ProductCategory category,
            Set<SkinType> skinTypes,
            String imageUrl,
            String purchaseUrl,
            Integer price,
            Integer originalPrice,
            Integer discountRate
    ) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.skinTypes = skinTypes == null ? new HashSet<>() : new HashSet<>(skinTypes);
        this.imageUrl = imageUrl;
        this.purchaseUrl = purchaseUrl;
        this.price = price;
        this.originalPrice = originalPrice;
        this.discountRate = discountRate;
        this.priceUpdatedAt = LocalDateTime.now();
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
