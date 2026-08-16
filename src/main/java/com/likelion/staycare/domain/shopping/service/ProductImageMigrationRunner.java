package com.likelion.staycare.domain.shopping.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.shopping.image-migration", name = "enabled", havingValue = "true")
public class ProductImageMigrationRunner implements ApplicationRunner {

    private final ProductImageMigrationService productImageMigrationService;

    @Override
    public void run(ApplicationArguments args) {
        ProductImageMigrationService.MigrationResult result = productImageMigrationService.migrateProductImages();
        log.info("Product image migration runner finished. total={}, migrated={}, failed={}",
                result.total(), result.migrated(), result.failed());
    }
}
