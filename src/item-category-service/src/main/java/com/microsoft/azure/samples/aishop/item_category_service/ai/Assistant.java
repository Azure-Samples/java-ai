package com.microsoft.azure.samples.aishop.item_category_service.ai;

import com.microsoft.azure.samples.java_ai.common.dto.ItemCategoryDto;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface Assistant {

    @SystemMessage(PromptConstant.ITEM_CATEGORIZATION_SYSTEM_PROMPT)
    ItemCategoryDto categorizeItem(String userPrompt);
    
}
