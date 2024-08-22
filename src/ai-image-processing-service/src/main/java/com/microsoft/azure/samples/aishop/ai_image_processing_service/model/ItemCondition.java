package com.microsoft.azure.samples.aishop.ai_image_processing_service.model;

public enum ItemCondition {
    
    NEW("New"),
    LIKE_NEW("Like New"),
    USED("Used"),
    REFURBISHED("Refurbished");

    private String label;

    private ItemCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ItemCondition fromString(String label) {
        for (ItemCondition itemCondition : ItemCondition.values()) {
            if (itemCondition.label.equalsIgnoreCase(label)) {
                return itemCondition;
            }
        }
        return null;
    }
}