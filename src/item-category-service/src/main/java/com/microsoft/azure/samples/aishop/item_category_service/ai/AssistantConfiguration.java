package com.microsoft.azure.samples.aishop.item_category_service.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import com.azure.identity.DefaultAzureCredentialBuilder;

@Configuration
public class AssistantConfiguration {

    @Value("${langchain4j.azure-open-ai.chat-model.endpoint}")
    private String endpoint;

    @Value("${langchain4j.azure-open-ai.chat-model.deployment-nam}")
    private String deploymentName;

    /**
     * This chat memory will be used by an {@link Assistant}
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.withMaxMessages(10);
    }

    @Bean
	AzureOpenAiChatModel openAiChatModelWithMi() {
		return AzureOpenAiChatModel.builder()
			.endpoint(endpoint)
			.tokenCredential(new DefaultAzureCredentialBuilder().build())
			.deploymentName(deploymentName)
			.build();
	}

}
