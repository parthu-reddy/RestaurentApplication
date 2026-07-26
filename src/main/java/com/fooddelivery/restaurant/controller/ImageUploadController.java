package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.common.service.CloudflareR2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import net.coobird.thumbnailator.Thumbnails;
import java.io.ByteArrayOutputStream;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Slf4j
public class ImageUploadController {

    private final CloudflareR2Service cloudflareR2Service;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam("folderId") String folderId,
            @RequestParam(value = "imageType", defaultValue = "default") String imageType,
            Authentication authentication) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("A file must be provided (INVALID_INPUT)"));
        }
        
        // Sanitize folderId to prevent path traversal or weird characters
        String safeFolderId = folderId.replaceAll("[^a-zA-Z0-9_-]", "");
        
        // Prevent IDOR: Force the folder to be the user's ID unless they are an admin.
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            safeFolderId = authentication.getName();
        } else if (safeFolderId.isEmpty()) {
            safeFolderId = "default";
        }
        
        long MAX_DOWNLOAD_SIZE = 5 * 1024 * 1024; // 5 MB limit

        try {
            byte[] imageBytes;
            String contentType;
            String extension = ".jpg";

            if (file.getSize() > MAX_DOWNLOAD_SIZE) {
                 return ResponseEntity.badRequest().body(ApiResponse.error("File size exceeds 5MB limit (FILE_TOO_LARGE)"));
            }
            imageBytes = file.getBytes();
            contentType = file.getContentType();
            if (file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")) {
                extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            }


            // Compress image based on imageType
            imageBytes = compressImage(imageBytes, imageType);
            
            // SECURITY/INTEGRITY: compressImage always outputs a normalized JPEG payload. 
            // We must force the extension and contentType to match to prevent MIME-sniffing vulnerabilities or broken images.
            extension = ".jpg";
            contentType = "image/jpeg";

            // Generate UUID for filename
            String fileName = UUID.randomUUID().toString() + extension;

            // Upload to Cloudflare R2
            String publicUrl = cloudflareR2Service.uploadImage(imageBytes, safeFolderId, fileName, contentType);

            return ResponseEntity.ok(ApiResponse.success(publicUrl, "Image uploaded successfully"));
        } catch (Exception e) {
            log.error("Failed to upload image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload image: " + e.getMessage() + " (UPLOAD_FAILED)"));
        }
    }

    private byte[] compressImage(byte[] originalBytes, String imageType) throws Exception {
        long targetSizeKb = "menu".equalsIgnoreCase(imageType) ? 4 : 40;
        long targetSizeBytes = targetSizeKb * 1024;

        // SECURITY: Mitigate Image Bomb (Decompression Bomb) DoS attacks.
        // A 5MB highly-compressed PNG could expand to a 50,000x50,000 image in memory, consuming 7.5GB of RAM and crashing the server.
        // We verify the dimensions by reading ONLY the headers first.
        try (java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(originalBytes);
             javax.imageio.stream.ImageInputStream iis = javax.imageio.ImageIO.createImageInputStream(bais)) {
             
            java.util.Iterator<javax.imageio.ImageReader> readers = javax.imageio.ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                javax.imageio.ImageReader reader = readers.next();
                try {
                    reader.setInput(iis, true);
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);
                    if (width > 5000 || height > 5000) {
                        throw new Exception("Image dimensions (" + width + "x" + height + ") exceed the 5000x5000 safety limit.");
                    }
                } finally {
                    reader.dispose();
                }
            } else {
                throw new Exception("Unsupported or invalid image format.");
            }
        }

        // Match the frontend limits exactly to avoid double-shrinking!
        int maxWidth = "menu".equalsIgnoreCase(imageType) ? 400 : 1200;

        // Decode the image into a BufferedImage exactly ONCE.
        java.awt.image.BufferedImage originalImage;
        try (java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(originalBytes)) {
            originalImage = javax.imageio.ImageIO.read(bais);
            if (originalImage == null) {
                throw new Exception("Invalid image data");
            }
        }

        // 1. SAFE NORMALIZATION (Strips metadata, forces JPEG, honors frontend dimensions)
        // Even if the image is already small enough, we MUST re-encode it to strip EXIF malware/polyglots.
        byte[] normalizedBytes;
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            net.coobird.thumbnailator.Thumbnails.of(originalImage)
                    .size(maxWidth, maxWidth) // Honors max size, won't upscale if smaller
                    .outputFormat("jpg")
                    .outputQuality(0.9f)
                    .toOutputStream(baos);
            normalizedBytes = baos.toByteArray();
        }

        // If the safely stripped image is small enough, return immediately!
        if (normalizedBytes.length <= targetSizeBytes) {
            return normalizedBytes;
        }

        // 2. AGGRESSIVE COMPRESSION LOOP (Fallback)
        float quality = 0.8f;
        byte[] currentBytes = normalizedBytes;

        for (int i = 0; i < 5; i++) { 
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                net.coobird.thumbnailator.Thumbnails.of(originalImage)
                        .size(maxWidth, maxWidth)
                        .outputFormat("jpg")
                        .outputQuality(quality)
                        .toOutputStream(baos);

                currentBytes = baos.toByteArray();
                if (currentBytes.length <= targetSizeBytes) {
                    return currentBytes;
                }
            }

            maxWidth = (int) (maxWidth * 0.8); // 80% reduction instead of brutal 50%
            quality -= 0.15f;
            if (quality < 0.1f) quality = 0.1f;
        }

        // If it still fails, throw an exception
        if (currentBytes.length > targetSizeBytes) {
            throw new Exception("Unable to compress image below the target size of " + targetSizeKb + "KB");
        }

        return currentBytes;
    }
}
