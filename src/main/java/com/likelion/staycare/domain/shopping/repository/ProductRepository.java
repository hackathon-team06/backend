package com.likelion.staycare.domain.shopping.repository;

import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.shopping.entity.Product;
import com.likelion.staycare.domain.shopping.entity.ProductCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndIsActiveTrue(Long productId);

    @EntityGraph(attributePaths = "skinTypes")
    @Query("""
            select distinct p
            from Product p
            join p.skinTypes st
            where p.isActive = true
              and st = :skinType
              and p.category = :category
            """)
    List<Product> findActiveProductsBySkinTypeAndCategory(
            @Param("skinType") SkinType skinType,
            @Param("category") ProductCategory category
    );

    @Query(
            value = """
                    select p.product_id
                    from products p
                    where p.is_active = true
                    order by rand()
                    limit 5
                    """,
            nativeQuery = true
    )
    List<Long> findRandomActiveProductIds();

    @EntityGraph(attributePaths = "skinTypes")
    @Query("""
            select distinct p
            from Product p
            where p.id in :productIds
            """)
    List<Product> findAllByIdInWithSkinTypes(@Param("productIds") List<Long> productIds);
}
