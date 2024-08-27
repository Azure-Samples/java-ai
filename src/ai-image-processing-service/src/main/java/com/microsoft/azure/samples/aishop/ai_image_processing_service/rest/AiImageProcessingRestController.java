package com.microsoft.azure.samples.aishop.ai_image_processing_service.rest;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.azure.openai.AzureOpenAiResponseFormat;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.Media;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.microsoft.azure.samples.aishop.ai_image_processing_service.ai.PromptConstant;
import com.microsoft.azure.samples.java_ai.common.dto.ItemInfoDto;

@RestController
public class AiImageProcessingRestController {
    
    private final AzureOpenAiChatModel chatModel;

    public AiImageProcessingRestController(final AzureOpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Retrieves the details of an item based on an image.
     *
     * @param imageBlobSasTokenUrl The URL of the image blob with a SAS token.
     * @param mimeType The MIME type of the image.
     * @return The details of the item as a JSON object corresponding to {@link ItemInfo}.
     * @throws MalformedURLException If the image blob URL is invalid. 
     */
    @PostMapping("/item-info")
    public ItemInfoDto getItemInfo(@RequestParam("imageBlobSasTokenUrl") final String imageBlobSasTokenUrl, @RequestParam("mimeType") final String mimeType) throws MalformedURLException {
        final AzureOpenAiChatOptions chatOptions = AzureOpenAiChatOptions.builder()
            .withDeploymentName("gpt-4o")
            .withTemperature(0f)
            .withTopP(1f)
            .withMaxTokens(2000)
            .withResponseFormat(AzureOpenAiResponseFormat.JSON)
            .build();
        final Media imageMedia = this.generateMedia(imageBlobSasTokenUrl, mimeType);
        final String answer = ChatClient.create(chatModel).prompt()
            .options(chatOptions)
            .system(PromptConstant.ITEM_DESCRIPTION_SYSTEM_PROMPT)
            .user(u -> u.text(PromptConstant.ITEM_DESCRIPTION_USER_PROMPT).media(imageMedia))
            .call()
            .content();
        final ItemInfoDto itemInfoDto = parseItemInfoDto(answer);
        return itemInfoDto;
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

    private ItemInfoDto parseItemInfoDto(final String answer) {
        final Gson gson = new Gson();
        return gson.fromJson(answer, ItemInfoDto.class);
    }
}