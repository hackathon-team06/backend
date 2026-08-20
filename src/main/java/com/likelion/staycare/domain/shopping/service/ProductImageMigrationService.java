package com.likelion.staycare.domain.shopping.service;

import com.likelion.staycare.domain.shopping.entity.Product;
import com.likelion.staycare.domain.shopping.repository.ProductRepository;
import com.likelion.staycare.global.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageMigrationService {

    private static final String DEFAULT_SOURCE_PREFIX = "https://image.oliveyoung.co.kr/";

    private final ProductRepository productRepository;
    private final S3Uploader s3Uploader;

    @Value("${app.shopping.image-migration.source-prefix:" + DEFAULT_SOURCE_PREFIX + "}")
    private String sourcePrefix;

    @Transactional
    public MigrationResult migrateProductImages() {
        List<Product> products = productRepository.findAllByIsActiveTrueAndImageUrlStartingWith(sourcePrefix);
        if (products.isEmpty()) {
            log.info("No product images matched source prefix={}", sourcePrefix);
            return new MigrationResult(0, 0, 0);
        }

        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        int migrated = 0;
        int failed = 0;

        for (Product product : products) {
            try {
                migrateSingleProductImage(httpClient, product);
                migrated++;
            } catch (Exception e) {
                failed++;
                log.error("Failed to migrate product image. productId={}, imageUrl={}",
                        product.getId(), product.getImageUrl(), e);
            }
        }

        log.info("Product image migration completed. total={}, migrated={}, failed={}",
                products.size(), migrated, failed);
        return new MigrationResult(products.size(), migrated, failed);
    }

    private void migrateSingleProductImage(HttpClient httpClient, Product product)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(product.getImageUrl()))
                .timeout(Duration.ofSeconds(20))
                .header("User-Agent", "StayCareImageMigrator/1.0")
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Image download failed with status " + response.statusCode());
        }

        String contentType = response.headers().firstValue("Content-Type").orElse(null);
        String originalFilename = extractFilename(product.getImageUrl(), contentType);
        S3Uploader.UploadResult uploadResult = s3Uploader.uploadProductImage(
                response.body(),
                contentType,
                originalFilename,
                product.getId()
        );

        product.updateImageUrl(uploadResult.url());
        log.info("Migrated product image. productId={}, migratedUrl={}", product.getId(), uploadResult.url());
    }

    private String extractFilename(String imageUrl, String contentType) {
        String sanitizedUrl = imageUrl == null ? "" : imageUrl.split("\\?")[0];
        int lastSlashIndex = sanitizedUrl.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex + 1 < sanitizedUrl.length()) {
            return sanitizedUrl.substring(lastSlashIndex + 1);
        }

        if ("image/png".equalsIgnoreCase(contentType)) {
            return "product.png";
        }
        if ("image/webp".equalsIgnoreCase(contentType)) {
            return "product.webp";
        }
        if ("image/gif".equalsIgnoreCase(contentType)) {
            return "product.gif";
        }
        return "product.jpg";
    }

    public record MigrationResult(int total, int migrated, int failed) {}
}
