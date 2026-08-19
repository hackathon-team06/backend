package com.likelion.staycare.domain.shopping.service;

import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.point.service.PointService;
import com.likelion.staycare.domain.shopping.dto.request.ProductCreateRequest;
import com.likelion.staycare.domain.shopping.dto.response.PointPriceResponse;
import com.likelion.staycare.domain.shopping.dto.response.ProductDetailResponse;
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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingService {

    private static final ProductCategory DEFAULT_CATEGORY = ProductCategory.SKIN_TONER;

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;
    private final PointService pointService;

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

        return ProductResponse.from(product, false, 0);
    }

    public List<ProductResponse> getProducts(Long userId, String skinType, String category) {
        User user = getUser(userId);
        SkinType resolvedSkinType = resolveSkinType(user, skinType);
        ProductCategory resolvedCategory = resolveCategory(category, DEFAULT_CATEGORY);
        int availablePoints = getAvailablePoints(userId);

        List<Product> products = productRepository.findActiveProductsBySkinTypeAndCategory(
                resolvedSkinType,
                resolvedCategory
        );
        Set<Long> likedProductIds = productLikeRepository.findLikedProductIdsByUserId(userId);

        return products.stream()
                .map(product -> ProductResponse.from(
                        product,
                        likedProductIds.contains(product.getId()),
                        availablePoints
                ))
                .toList();
    }

    public List<ProductResponse> getRandomProducts(Long userId) {
        getUser(userId);
        int availablePoints = getAvailablePoints(userId);

        List<Long> randomProductIds = productRepository.findRandomActiveProductIds();
        if (randomProductIds.isEmpty()) {
            return List.of();
        }

        Set<Long> likedProductIds = productLikeRepository.findLikedProductIdsByUserId(userId);
        Map<Long, Product> productMap = productRepository.findAllByIdInWithSkinTypes(randomProductIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return randomProductIds.stream()
                .map(productMap::get)
                .filter(product -> product != null && product.isActive())
                .map(product -> ProductResponse.from(
                        product,
                        likedProductIds.contains(product.getId()),
                        availablePoints
                ))
                .toList();
    }

    public PointPriceResponse getPointAppliedPrice(Long userId, Long productId, Integer usePoints) {
        if (usePoints == null || usePoints < 0) {
            throw new CustomException(ShoppingErrorCode.INVALID_USE_POINTS);
        }

        Product product = productRepository.findByIdAndIsActiveTrue(productId)
                .orElseThrow(() -> new CustomException(ShoppingErrorCode.PRODUCT_NOT_FOUND));

        int availablePoints = getAvailablePoints(userId);
        int usedPoints = Math.min(usePoints, Math.min(availablePoints, product.getPrice()));
        int pointAppliedPrice = product.getPrice() - usedPoints;

        return PointPriceResponse.builder()
                .productId(product.getId())
                .originalPrice(product.getOriginalPrice())
                .price(product.getPrice())
                .availablePoints(availablePoints)
                .requestedPoints(usePoints)
                .usedPoints(usedPoints)
                .pointDiscountAmount(usedPoints)
                .pointAppliedPrice(pointAppliedPrice)
                .build();
    }

    public ProductDetailResponse getProductDetail(Long userId, Long productId) {
        getUser(userId);

        Product product = productRepository.findActiveProductDetailById(productId)
                .orElseThrow(() -> new CustomException(ShoppingErrorCode.PRODUCT_NOT_FOUND));

        boolean liked = productLikeRepository.existsByUserIdAndProductId(userId, productId);
        return ProductDetailResponse.from(product, liked);
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

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findByIdAndIsActiveTrue(productId)
                .orElseThrow(() -> new CustomException(ShoppingErrorCode.PRODUCT_NOT_FOUND));

        product.deactivate();
    }

    public List<ProductResponse> getLikedProducts(Long userId, String category) {
        getUser(userId);
        ProductCategory resolvedCategory = resolveCategory(category, null);
        int availablePoints = getAvailablePoints(userId);

        List<Product> likedProducts = resolvedCategory == null
                ? productLikeRepository.findLikedProductsByUserId(userId)
                : productLikeRepository.findLikedProductsByUserIdAndCategory(userId, resolvedCategory);

        return likedProducts.stream()
                .map(product -> ProductResponse.from(product, true, availablePoints))
                .toList();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }

    private int getAvailablePoints(Long userId) {
        return pointService.getMyPoint(userId).getPoint();
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

    private ProductCategory resolveCategory(String category, ProductCategory defaultCategory) {
        if (category == null || category.isBlank()) {
            return defaultCategory;
        }

        try {
            return ProductCategory.from(category);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ShoppingErrorCode.INVALID_PRODUCT_CATEGORY);
        }
    }
}
