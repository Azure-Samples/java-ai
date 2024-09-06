package com.microsoft.azure.samples.aishop.item_category_service.ai;

import com.microsoft.azure.samples.java_ai.common.dto.ItemInfoDto;

public class PromptUtils {

    public static final String formatItemCategorizationUserPrompt(final ItemInfoDto itemInfoDto) {
        return PromptConstant.ITEM_CATEGORIZATION_USER_PROMPT.replace("{itemLabel}", itemInfoDto.getLabel())
                .replace("{brand}", itemInfoDto.getBrand()).replace("{model}", itemInfoDto.getModel())
                .replace("{description}", itemInfoDto.getDescription());
    }

    private PromptUtils() {
    }
    
}
