package com.likelion.staycare.domain.shopping.repository;

import com.likelion.staycare.domain.shopping.entity.Product;
import com.likelion.staycare.domain.shopping.entity.ProductCategory;
import com.likelion.staycare.domain.shopping.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<ProductLike> findByUserIdAndProductId(Long userId, Long productId);

    @Query("""
            select pl.product.id
            from ProductLike pl
            where pl.user.id = :userId
            """)
    Set<Long> findLikedProductIdsByUserId(@Param("userId") Long userId);

    @Query("""
            select distinct p
            from ProductLike pl
            join pl.product p
            left join fetch p.skinTypes
            where pl.user.id = :userId
              and p.isActive = true
            """)
    List<Product> findLikedProductsByUserId(@Param("userId") Long userId);

    @Query("""
            select distinct p
            from ProductLike pl
            join pl.product p
            left join fetch p.skinTypes
            where pl.user.id = :userId
              and p.isActive = true
              and p.category = :category
            """)
    List<Product> findLikedProductsByUserIdAndCategory(
            @Param("userId") Long userId,
            @Param("category") ProductCategory category
    );
}
