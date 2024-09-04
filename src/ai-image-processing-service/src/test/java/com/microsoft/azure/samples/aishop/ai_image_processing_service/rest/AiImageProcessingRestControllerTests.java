package com.microsoft.azure.samples.aishop.ai_image_processing_service.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.samples.aishop.ai_image_processing_service.ai.PromptConstant;
import com.microsoft.azure.samples.java_ai.common.dto.ItemCondition;
import com.microsoft.azure.samples.java_ai.common.dto.ItemInfoDto;

@RunWith(SpringRunner.class)
@WebMvcTest(AiImageProcessingRestController.class)
public class AiImageProcessingRestControllerTests {

    @MockBean
    private AzureOpenAiChatModel chatModel;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnItemInfoWithStatusOk() throws Exception {
        // Mock the response from the ChatClient
        final ChatClient chatClient = Mockito.mock(ChatClient.class);
        final ChatClientRequestSpec chatClientRequestSpec = Mockito.mock(ChatClientRequestSpec.class);
        final CallResponseSpec callResponseSpec = Mockito.mock(CallResponseSpec.class);
        
        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.options(any(AzureOpenAiChatOptions.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.system(any(String.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(any(Consumer.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);

        // Mock the answer
        final String answer = "{\"label\":\"Item Name\",\"price\":10.0,\"brand\":\"Brand Name\",\"model\":\"Model Name\",\"condition\":\"New\",\"description\":\"Item Description\"}";
        when(callResponseSpec.content()).thenReturn(answer);

        // Mock the ChatClient creation
        @SuppressWarnings("unused")
        final MockedStatic<ChatClient> chatClientMockedStatic = Mockito.mockStatic(ChatClient.class);
        when(ChatClient.create(chatModel)).thenReturn(chatClient);

        // Call the method under test
        // ItemInfoDto result = controller.getItemInfo("http://myblob.com/imageBlobSasTokenUrl", "image/jpeg");
        final MvcResult result = mockMvc
            .perform(post("/item-info")
                .param("imageBlobSasTokenUrl", "http://myblob.com/imageBlobSasTokenUrl")
                .param("mimeType", "image/jpeg")
            )
            .andExpect(status().isOk())
            .andReturn();
        final ItemInfoDto actualItemInfoDto = objectMapper
            .readValue(result.getResponse().getContentAsString(), ItemInfoDto.class);

        // Verify the result
        assertEquals("Item Name", actualItemInfoDto.getLabel());
        assertEquals("Brand Name", actualItemInfoDto.getBrand());
        assertEquals("Model Name", actualItemInfoDto.getModel());
        assertEquals(ItemCondition.NEW, actualItemInfoDto.getCondition());
        assertEquals(10.0, actualItemInfoDto.getPrice(), 0.001);
        assertEquals("Item Description", actualItemInfoDto.getDescription());
        verify(chatClient, times(1)).prompt();
        verify(chatClientRequestSpec, times(1)).options(any(AzureOpenAiChatOptions.class));
        verify(chatClientRequestSpec, times(1)).system(PromptConstant.ITEM_DESCRIPTION_SYSTEM_PROMPT);
        verify(chatClientRequestSpec, times(1)).user(any(Consumer.class));
        verify(chatClientRequestSpec, times(1)).call();
    }

    @Test
    public void shouldReturnBadRequestWhenImageBlobSasTokenUrlIsNull() throws Exception {
        mockMvc
            .perform(post("/item-info")
                .param("mimeType", "image/jpeg")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenImageBlobSasTokenUrlIsEmpty() throws Exception {
        mockMvc
            .perform(post("/item-info")
                .param("imageBlobSasTokenUrl", "")
                .param("mimeType", "image/jpeg")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenImageBlobSasTokenUrlIsNotAWellFormedUrl() throws Exception {
        mockMvc
            .perform(post("/item-info")
                .param("imageBlobSasTokenUrl", "not-a-well-formed-url")
                .param("mimeType", "image/jpeg")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenMimeTypeIsNull() throws Exception {
        mockMvc
            .perform(post("/item-info")
                .param("imageBlobSasTokenUrl", "http://myblob.com/imageBlobSasTokenUrl")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenMimeTypeIsEmpty() throws Exception {
        mockMvc
            .perform(post("/item-info")
                .param("imageBlobSasTokenUrl", "http://myblob.com/imageBlobSasTokenUrl")
                .param("mimeType", "")
            )
            .andExpect(status().isBadRequest());
    }
}