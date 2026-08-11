package com.likelion.staycare.domain.shopping.service;

import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.shopping.dto.request.ProductCreateRequest;
import com.likelion.staycare.domain.shopping.dto.response.ProductResponse;
import com.likelion.staycare.domain.shopping.entity.Product;
import com.likelion.staycare.domain.shopping.entity.ProductCategory;
import com.likelion.staycare.domain.shopping.entity.ProductLike;
import com.likelion.staycare.domain.shopping.exception.ShoppingErrorCode;
import com.likelion.staycare.domain.shopping.repository.ProductLikeRepository;
import com.likelion.staycare.domain.shopping.repository.ProductRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.exception.UserErrorCode;
import com.likelion.staycare.domain.user.repository.UserRepository;
import com.likelion.staycare.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingService {

    private static final ProductCategory DEFAULT_CATEGORY = ProductCategory.SKIN_TONER;

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = productRepository.save(
                Product.builder()
                        .name(request.name())
                        .brand(request.brand())
                        .category(request.category())
                        .skinTypes(request.skinTypes())
                        .imageUrl(request.imageUrl())
                        .purchaseUrl(request.purchaseUrl())
                        .price(request.price())
                        .originalPrice(request.originalPrice())
                        .discountRate(request.discountRate())
                        .build()
        );

        return ProductResponse.from(product, false);
    }

    public List<ProductResponse> getProducts(Long userId, String skinType, ProductCategory category) {
        User user = getUser(userId);
        SkinType resolvedSkinType = resolveSkinType(user, skinType);
        ProductCategory resolvedCategory = category == null ? DEFAULT_CATEGORY : category;

        List<Product> products = productRepository.findActiveProductsBySkinTypeAndCategory(
                resolvedSkinType,
                resolvedCategory
        );
        Set<Long> likedProductIds = productLikeRepository.findLikedProductIdsByUserId(userId);

        return products.stream()
                .map(product -> ProductResponse.from(product, likedProductIds.contains(product.getId())))
                .toList();
    }

    @Transactional
    public void likeProduct(Long userId, Long productId) {
        User user = getUser(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ShoppingErrorCode.PRODUCT_NOT_FOUND));

        if (!product.isActive()) {
            throw new CustomException(ShoppingErrorCode.PRODUCT_INACTIVE);
        }

        if (productLikeRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new CustomException(ShoppingErrorCode.PRODUCT_LIKE_ALREADY_EXISTS);
        }

        productLikeRepository.save(
                ProductLike.builder()
                        .user(user)
                        .product(product)
                        .build()
        );
    }

    @Transactional
    public void unlikeProduct(Long userId, Long productId) {
        ProductLike productLike = productLikeRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new CustomException(ShoppingErrorCode.PRODUCT_LIKE_NOT_FOUND));

        productLikeRepository.delete(productLike);
    }

    public List<ProductResponse> getLikedProducts(Long userId, ProductCategory category) {
        getUser(userId);

        List<Product> likedProducts = category == null
                ? productLikeRepository.findLikedProductsByUserId(userId)
                : productLikeRepository.findLikedProductsByUserIdAndCategory(userId, category);

        return likedProducts.stream()
                .map(product -> ProductResponse.from(product, true))
                .toList();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }

    private SkinType resolveSkinType(User user, String skinType) {
        if (skinType != null && !skinType.isBlank()) {
            try {
                return SkinType.from(skinType);
            } catch (IllegalArgumentException e) {
                throw new CustomException(ShoppingErrorCode.INVALID_SKIN_TYPE);
            }
        }

        if (!user.hasDiagnosis()) {
            throw new CustomException(ShoppingErrorCode.SKIN_TYPE_REQUIRED);
        }

        return user.getSkinType();
    }
}
