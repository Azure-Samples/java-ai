package com.microsoft.azure.samples.aishop.ai_image_processing_service.rest;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.azure.samples.aishop.ai_image_processing_service.ai.PromptConstant; 

@RestController
public class AiImageProcessingRestController {
    
    private final AzureOpenAiChatModel chatModel;

    @Autowired AiImageProcessingRestController(final AzureOpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Retrieves the details of an item based on an image.
     *
     * @param imageBlobSasTokenUrl The URL of the image blob with a SAS token.
     * @param mimeType The MIME type of the image.
     * @return The details of the item.
     * @throws MalformedURLException If the image blob URL is invalid. 
     */
    @PostMapping("/item-info")
    public String getItemInfo(@RequestParam("imageBlobSasTokenUrl") final String imageBlobSasTokenUrl, @RequestParam("mimeType") final String mimeType) throws MalformedURLException {
        System.out.println("imageBlobSasTokenUrl: " + imageBlobSasTokenUrl);
        final AzureOpenAiChatOptions chatOptions = AzureOpenAiChatOptions.builder()
            .withDeploymentName("gpt-4o")
            .withTemperature(0f)
            .withTopP(1f)
            .build();
        System.out.println("chatOptions: " + chatOptions);
        final Media imageMedia = this.generateMedia(imageBlobSasTokenUrl, mimeType);
        final String answer = ChatClient.create(chatModel).prompt()
            .options(chatOptions)
            .system(PromptConstant.ITEM_DESCRIPTION_SYSTEM_PROMPT)
            .user(u -> u.text(PromptConstant.ITEM_DESCRIPTION_USER_PROMPT).media(imageMedia))
            .call()
            .content();
        System.out.println("answer: " + answer);
        return answer;
    }

    /**
     * Creates a new Media object based on the provided image blob URL and MIME type.
     *
     * @param imageBlobUrl The URL of the image blob.
     * @param mimeTypeAsString The MIME type of the image as a string.
     * @return A new Media object representing the image.
     * @throws MalformedURLException 
     */
    private Media generateMedia(final String imageBlobUrl, final String mimeTypeAsString) throws MalformedURLException {
        final URL url = new URL(imageBlobUrl);
        final MimeType mimeType = MimeType.valueOf(mimeTypeAsString);
        return new Media(mimeType, url);
    }
}