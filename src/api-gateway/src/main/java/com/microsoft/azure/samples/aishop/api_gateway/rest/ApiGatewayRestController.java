package com.microsoft.azure.samples.aishop.api_gateway.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.azure.samples.aishop.api_gateway.rest.client.AiImageProcessingServiceClient;
import com.microsoft.azure.samples.aishop.api_gateway.rest.client.BlobStorageServiceClient;

@RestController
public class ApiGatewayRestController {

  @Autowired
  private BlobStorageServiceClient blobStorageServiceClient;
  @Autowired
  private AiImageProcessingServiceClient aiImageProcessingServiceClient;

  @PostMapping("/item-info")
  public String getItemInfo(@RequestParam("image") final MultipartFile image) {
    final String blobName = blobStorageServiceClient.uploadFile(image);
    final String blobSasTokenUrl = blobStorageServiceClient.getSasTokenUrl(blobName, 500);
    final String itemInfo = aiImageProcessingServiceClient.getItemInfo(blobSasTokenUrl, image.getContentType());
    return itemInfo;
  }
  
}
