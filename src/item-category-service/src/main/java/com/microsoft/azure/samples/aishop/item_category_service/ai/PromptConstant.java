package com.microsoft.azure.samples.aishop.item_category_service.ai;

public class PromptConstant {
    public static final String ITEM_CATEGORIZATION_SYSTEM_PROMPT = """
        You are an expert in the field of e-commerce item categorization.
        Using tools at your disposal, you return a json with item category, subcategory, and sub-subcategory.

        You received as input the label of the item, its brand, its model and its description.
        Both the brand and the model can be `unknown`.
        
        The returned json should have the following structure:
        {
            "category": "Item category",
            "subcategory": "Item subcategory",
            "level2Subcategory": "Item sub-subcategory"
        }

        Return only the JSON string. Do not include any other information.
        """;

    public static final String ITEM_CATEGORIZATION_USER_PROMPT = """
            Item label: {itemLabel}
            Brand: {brand}
            Model: {model}
            
            Description:
            {description}
            """;
    
    private PromptConstant() {
    }
}
