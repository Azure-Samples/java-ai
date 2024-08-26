package com.microsoft.azure.samples.aishop.api_gateway.rest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.microsoft.azure.samples.aishop.ai_image_processing_service.dto.ItemInfoDto;

@FeignClient(value = "ai-image-processing-service")
public interface AiImageProcessingServiceClient {
  @RequestMapping(method = RequestMethod.POST, value = "/item-info")
  ItemInfoDto getItemInfo(@RequestParam("imageBlobSasTokenUrl") final String imageBlobSasTokenUrl, @RequestParam("mimeType") final String mimeType);
}
