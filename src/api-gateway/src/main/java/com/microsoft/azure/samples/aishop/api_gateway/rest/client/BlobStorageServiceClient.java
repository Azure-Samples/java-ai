package com.microsoft.azure.samples.aishop.api_gateway.rest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient("blob-storage-service")
public interface BlobStorageServiceClient {
    @RequestMapping(method = RequestMethod.POST, value = "/upload", consumes = "multipart/form-data")
    String uploadFile(@RequestPart("file") final MultipartFile file);

    @RequestMapping(method = RequestMethod.POST, value = "/sas-token")
    String getSasTokenUrl(@RequestParam("blobName") final String blobName, @RequestParam("durationInSeconds") final long durationInSeconds);
}
