package com.microsoft.azure.samples.aishop.blob_storage_service.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.azure.samples.aishop.blob_storage_service.exception.WriteBlobException;

@RestController
@Profile("local")
public class LocalBlobStorageRestController {

    private final Path storageDir;

    public LocalBlobStorageRestController() throws IOException {
        this.storageDir = Files.createTempDirectory("local-blob-storage");
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") final MultipartFile file) throws WriteBlobException {
        final String originalFilename = file.getOriginalFilename();
        final int lastDot = originalFilename.lastIndexOf(".");
        final String ext = lastDot >= 0 ? originalFilename.substring(lastDot) : "";
        final String baseName = lastDot >= 0 ? originalFilename.substring(0, lastDot) : originalFilename;
        final String uniqueName = baseName + "-" + UUID.randomUUID().toString().substring(0, 5) + ext;

        try {
            Files.write(storageDir.resolve(uniqueName), file.getBytes());
        } catch (IOException e) {
            throw new WriteBlobException(e);
        }
        return uniqueName;
    }

    @PostMapping("/sas-token")
    public String getSasTokenUrl(
            @RequestParam("blobName") final String blobName,
            @RequestParam("durationInSeconds") final long durationInSeconds) {
        return "http://localhost:8082/local-files/" + blobName;
    }

    @GetMapping("/local-files/{blobName}")
    public ResponseEntity<Resource> serveFile(@PathVariable final String blobName) throws IOException {
        final Path filePath = storageDir.resolve(blobName).normalize();
        if (!filePath.startsWith(storageDir) || !Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        final Resource resource = new FileSystemResource(filePath);
        final String contentType = Files.probeContentType(filePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(resource);
    }
}
