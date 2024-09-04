package com.microsoft.azure.samples.aishop.api_gateway.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.samples.aishop.api_gateway.rest.client.AiImageProcessingServiceClient;
import com.microsoft.azure.samples.aishop.api_gateway.rest.client.BlobStorageServiceClient;
import com.microsoft.azure.samples.java_ai.common.dto.ItemCondition;
import com.microsoft.azure.samples.java_ai.common.dto.ItemInfoDto;

@RunWith(SpringRunner.class)
@WebMvcTest(ApiGatewayRestController.class)
public class ApiGatewayRestControllerTests {
    
    @MockBean
    private BlobStorageServiceClient blobStorageServiceClient;
    
    @MockBean
    private AiImageProcessingServiceClient aiImageProcessingServiceClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void shouldReturnItemInfoWithStatusOk() throws Exception {
        // Expected results
        final String expectedBlobName = "testBlobName";
        final String expectedBlobSasTokenUrl = "testBlobSasTokenUrl";
        ItemInfoDto expectedItemInfoDto = new ItemInfoDto(
            "Item Name","Brand Name", "Model Name",
            ItemCondition.NEW, 10.0, "Item Description"
        );
        
        // Mocked request parameters
        final MockMultipartFile image = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        
        // Mocked behavior
        when(blobStorageServiceClient.uploadFile(image)).thenReturn(expectedBlobName);
        when(blobStorageServiceClient.getSasTokenUrl(expectedBlobName, 500)).thenReturn(expectedBlobSasTokenUrl);
        when(aiImageProcessingServiceClient.getItemInfo(expectedBlobSasTokenUrl, image.getContentType())).thenReturn(expectedItemInfoDto);
        
        // Perform the test
        final MvcResult result = mockMvc
            .perform(multipart("/item-info").file(image))
            .andExpect(status().isOk())
            .andReturn();
        final ItemInfoDto actualItemInfoDto = objectMapper
            .readValue(result.getResponse().getContentAsString(), ItemInfoDto.class);
        
        // Verify the results
        assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        assertEquals(expectedItemInfoDto.getLabel(), actualItemInfoDto.getLabel());
        assertEquals(expectedItemInfoDto.getBrand(), actualItemInfoDto.getBrand());
        assertEquals(expectedItemInfoDto.getModel(), actualItemInfoDto.getModel());
        assertEquals(expectedItemInfoDto.getCondition(), actualItemInfoDto.getCondition());
        assertEquals(expectedItemInfoDto.getPrice(), actualItemInfoDto.getPrice(), 0.001);
        assertEquals(expectedItemInfoDto.getDescription(), actualItemInfoDto.getDescription());
        verify(blobStorageServiceClient, times(1)).uploadFile(image);
        verify(blobStorageServiceClient, times(1)).getSasTokenUrl(expectedBlobName, 500);
        verify(aiImageProcessingServiceClient, times(1)).getItemInfo(expectedBlobSasTokenUrl, image.getContentType());
    }

    @Test
    public void shouldReturnBadRequestWhenImageIsNull() throws Exception {
        // Perform the test
        mockMvc
            .perform(multipart("/item-info"))
            .andExpect(status().isBadRequest());
    }
}