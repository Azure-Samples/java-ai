package com.microsoft.azure.samples.java_ai.common.dto;

import com.google.gson.annotations.SerializedName;

public enum ItemCondition {
    
    @SerializedName("New")
    NEW("New"),
    @SerializedName("Like New")
    LIKE_NEW("Like New"),
    @SerializedName("Used")
    USED("Used"),
    @SerializedName("Refurbished")
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