package com.microsoft.azure.samples.aishop.api_gateway.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.azure.samples.aishop.api_gateway.rest.client.AiImageProcessingServiceClient;
import com.microsoft.azure.samples.aishop.api_gateway.rest.client.BlobStorageServiceClient;
import com.microsoft.azure.samples.java_ai.common.dto.ItemInfoDto;

import jakarta.validation.constraints.NotNull;

@RestController
public class ApiGatewayRestController {
    
    private final BlobStorageServiceClient blobStorageServiceClient;
    private final AiImageProcessingServiceClient aiImageProcessingServiceClient;
    
    public ApiGatewayRestController(final BlobStorageServiceClient blobStorageServiceClient,
    final AiImageProcessingServiceClient aiImageProcessingServiceClient) {
        this.blobStorageServiceClient = blobStorageServiceClient;
        this.aiImageProcessingServiceClient = aiImageProcessingServiceClient;
    }
    
    @PostMapping("/item-info")
    public ItemInfoDto getItemInfo(@RequestParam("image") @NotNull final MultipartFile image) {
        final String blobName = blobStorageServiceClient.uploadFile(image);
        final String blobSasTokenUrl = blobStorageServiceClient.getSasTokenUrl(blobName, 500);
        final ItemInfoDto itemInfoDto = aiImageProcessingServiceClient.getItemInfo(blobSasTokenUrl, image.getContentType());
        return itemInfoDto;
    }
    
}
