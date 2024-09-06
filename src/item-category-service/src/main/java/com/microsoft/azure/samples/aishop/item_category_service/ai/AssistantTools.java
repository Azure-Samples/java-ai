package com.microsoft.azure.samples.aishop.item_category_service.ai;

import java.util.List;

import org.springframework.stereotype.Component;

import com.microsoft.azure.samples.aishop.item_category_service.model.Category;
import com.microsoft.azure.samples.aishop.item_category_service.model.Level2Subcategory;
import com.microsoft.azure.samples.aishop.item_category_service.model.Subcategory;
import com.microsoft.azure.samples.aishop.item_category_service.repository.CategoryRepository;

import dev.langchain4j.agent.tool.Tool;

@Component
class AssistantTools {

    private final CategoryRepository categoryRepository;

    public AssistantTools(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Tool("Give the list of items' categories.")
    List<String> categories() {
        return categoryRepository
            .findAllByOrderByNameAsc()
            .stream()
            .map(Category::getName)
            .toList();
    }

    @Tool("Give the list of subcategories of a category.")
    List<String> subcategories(final String categoryName) {
        final Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            return List.of("Unknown category");
        }
        return category.getSubcategories()
            .stream()
            .map(Subcategory::getName)
            .toList();
    }

    @Tool("Given the list of level 2 subcategories of a a subcategory.")
    List<String> level2Subcategories(final String categoryName, final String subcategoryName) {
        final Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            return List.of("Unknown category");
        }
        final Subcategory subcategory = category.getSubcategories()
            .stream()
            .filter(s -> s.getName().equals(subcategoryName))
            .findFirst()
            .orElse(null);
        if (subcategory == null) {
            return List.of("Unknown subcategory");
        }
        return subcategory.getLevel2Subcategories()
            .stream()
            .map(Level2Subcategory::getName)
            .toList();
    }

}