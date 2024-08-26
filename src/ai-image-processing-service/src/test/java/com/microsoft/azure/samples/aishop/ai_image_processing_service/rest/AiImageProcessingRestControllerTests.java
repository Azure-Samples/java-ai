package com.microsoft.azure.samples.aishop.ai_image_processing_service.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import org.springframework.test.context.junit4.SpringRunner;

import com.microsoft.azure.samples.java_ai.common.dto.ItemInfoDto;

@RunWith(SpringRunner.class)
public class AiImageProcessingRestControllerTests {

    @Mock
    private AzureOpenAiChatModel chatModel;

    @SuppressWarnings("unchecked")
    @Test
    public void testGetItemInfo() throws MalformedURLException {
        AiImageProcessingRestController controller = new AiImageProcessingRestController(chatModel);

        // Mock the response from the ChatClient
        ChatClient chatClient = Mockito.mock(ChatClient.class);
        ChatClientRequestSpec chatClientRequestSpec = Mockito.mock(ChatClientRequestSpec.class);
        CallResponseSpec callResponseSpec = Mockito.mock(CallResponseSpec.class);
        
        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.options(any(AzureOpenAiChatOptions.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.system(any(String.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(any(Consumer.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);

        // Mock the answer
        String answer = "{\"label\":\"Item Name\",\"price\":10.0}";
        when(callResponseSpec.content()).thenReturn(answer);

        // Mock the ChatClient creation
        @SuppressWarnings("unused")
        MockedStatic<ChatClient> chatClientMockedStatic = Mockito.mockStatic(ChatClient.class);
        when(ChatClient.create(chatModel)).thenReturn(chatClient);

        // Call the method under test
        ItemInfoDto result = controller.getItemInfo("http://myblob.com/imageBlobSasTokenUrl", "image/jpeg");

        // Verify the result
        assertEquals("Item Name", result.getLabel());
        assertEquals(10.0, result.getPrice(), 0.001);
    }
}