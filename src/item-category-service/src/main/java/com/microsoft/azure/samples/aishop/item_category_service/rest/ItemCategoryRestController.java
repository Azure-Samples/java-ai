package com.microsoft.azure.samples.aishop.item_category_service.rest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.samples.aishop.item_category_service.ai.Assistant;
import com.microsoft.azure.samples.aishop.item_category_service.ai.PromptUtils;
import com.microsoft.azure.samples.aishop.item_category_service.model.Category;
import com.microsoft.azure.samples.aishop.item_category_service.model.Level2Subcategory;
import com.microsoft.azure.samples.aishop.item_category_service.model.Subcategory;
import com.microsoft.azure.samples.aishop.item_category_service.repository.CategoryRepository;
import com.microsoft.azure.samples.aishop.item_category_service.repository.Level2SubcategoryRepository;
import com.microsoft.azure.samples.aishop.item_category_service.repository.SubcategoryRepository;
import com.microsoft.azure.samples.java_ai.common.dto.ItemCategoryDto;
import com.microsoft.azure.samples.java_ai.common.dto.ItemInfoDto;

@RestController()
@RequestMapping("/categories")
public class ItemCategoryRestController {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final Level2SubcategoryRepository level2SubcategoryRepository;

    private final ObjectMapper objectMapper;

    private final Assistant assistant;

    public ItemCategoryRestController(final CategoryRepository categoryRepository,
                                              final SubcategoryRepository subcategoryRepository,
                                              final Level2SubcategoryRepository level2SubcategoryRepository,
                                           final ObjectMapper objectMapper,
                                              final Assistant assistant) {
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.level2SubcategoryRepository = level2SubcategoryRepository;
        this.objectMapper = objectMapper;
        this.assistant = assistant;
    }

    @GetMapping()
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{categoryId}/subcategories")
    public List<Subcategory> getSubcategories(@RequestParam(value = "categoryId") long categoryId) {
        return subcategoryRepository.findByCategoryId(categoryId);
    }

    @GetMapping("/{categoryId}/subcategories/{subcategoryId}/level2-subcategories")
    public List<Level2Subcategory> getLevel2Subcategories(@RequestParam(value = "subcategoryId") long subcategoryId) {
        return level2SubcategoryRepository.findBySubcategoryId(subcategoryId);
    }

    @PostMapping("/bootstrap")
    public void bootstrap() throws StreamReadException, DatabindException, IOException {
        final File  file = ResourceUtils.getFile("classpath:categories.json");
        final List<Category> categories = objectMapper.readValue(file, new TypeReference<List<Category>>() {});
        associateCategoriesSubcategoriesAndLevel2Subcategories(categories);
        categories.forEach(categoryRepository::save);
    }

    @PostMapping("/ai-item-categorization")
    public ItemInfoDto categorizeItem(@RequestBody ItemInfoDto itemInfoDto) {
        final ItemCategoryDto ItemCategoryDto =
            assistant.categorizeItem(PromptUtils.formatItemCategorizationUserPrompt(itemInfoDto));
        itemInfoDto.setCategory(ItemCategoryDto);
        return itemInfoDto;
    }
    
    /**
     * Links the categories, subcategories and level 2 subcategories
     * by setting the appropriate relationships. This is necessary
     * for the JPA entities to be correctly saved to the database
     * using {@link CascadeType.ALL}.
     * 
     * @param categories The list of categories to link.
     */
    private void associateCategoriesSubcategoriesAndLevel2Subcategories(final List<Category> categories) {
        categories.forEach(category -> {
            category.getSubcategories().forEach(subcategory -> {
                subcategory.setCategory(category);
                subcategory.getLevel2Subcategories().forEach(level2Subcategory -> {
                    level2Subcategory.setSubcategory(subcategory);
                });
            });
        });
    }
}
