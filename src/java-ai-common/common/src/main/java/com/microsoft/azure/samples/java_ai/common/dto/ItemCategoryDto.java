package com.microsoft.azure.samples.java_ai.common.dto;

import java.util.Objects;

public class ItemCategoryDto {
    private String category;
    private String subcategory;
    private String level2Subcategory;

    public ItemCategoryDto() {
    }

    public ItemCategoryDto(String category, String subcategory, String level2Subcategory) {
        this.category = category;
        this.subcategory = subcategory;
        this.level2Subcategory = level2Subcategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getLevel2Subcategory() {
        return level2Subcategory;
    }

    public void setLevel2Subcategory(String level2Subcategory) {
        this.level2Subcategory = level2Subcategory;
    }

    @Override
    public String toString() {
        return "ItemCategoryDto{" +
                "category='" + category + '\'' +
                ", subcategory='" + subcategory + '\'' +
                ", level2Subcategory='" + level2Subcategory + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, subcategory, level2Subcategory);
    }
}
