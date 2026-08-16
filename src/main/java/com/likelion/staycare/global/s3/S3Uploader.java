package com.likelion.staycare.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public UploadResult uploadProfileImage(MultipartFile file, Long userId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);

        String key = "profile-images/" + userId + "/" + UUID.randomUUID() + "." + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.getBytes())
        );

        String imageUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;

        return new UploadResult(key, imageUrl);
    }

    public UploadResult uploadProductImage(
            byte[] bytes,
            String contentType,
            String originalFilename,
            Long productId
    ) {
        String extension = extractExtension(originalFilename);
        String key = "product-images/" + productId + "/" + UUID.randomUUID() + "." + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(resolveContentType(contentType, extension))
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(bytes)
        );

        String imageUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
        return new UploadResult(key, imageUrl);
    }

    public void deleteFile(String key) {
        if (key == null || key.isBlank()) return;

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private String resolveContentType(String contentType, String extension) {
        if (contentType != null && !contentType.isBlank()) {
            return contentType;
        }

        return switch (extension.toLowerCase(Locale.ROOT)) {
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            case "gif" -> "image/gif";
            default -> "image/jpeg";
        };
    }

    public record UploadResult(String key, String url) {}
}
