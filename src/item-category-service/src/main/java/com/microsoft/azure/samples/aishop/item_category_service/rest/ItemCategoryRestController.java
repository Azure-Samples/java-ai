package com.microsoft.azure.samples.aishop.item_category_service.rest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import dev.langchain4j.memory.ChatMemory;

@RestController()
@RequestMapping("/categories")
public class ItemCategoryRestController {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final Level2SubcategoryRepository level2SubcategoryRepository;
    private final ObjectMapper objectMapper;
    private final Assistant assistant;
    private final ChatMemory chatMemory;

    /**
     * REST controller for managing item categories.
     * The constructor initializes the controller with
     * the necessary repositories and services. Then, it
     * bootstraps the data in the database.
     *
     * @param categoryRepository the repository for managing categories
     * @param subcategoryRepository the repository for managing subcategories
     * @param level2SubcategoryRepository the repository for managing level 2 subcategories
     * @param objectMapper the object mapper for JSON processing
     * @param assistant the assistant service for AI interactions
     * @param chatMemory the chat memory service for storing chat history
     */
    public ItemCategoryRestController(final CategoryRepository categoryRepository,
                                              final SubcategoryRepository subcategoryRepository,
                                              final Level2SubcategoryRepository level2SubcategoryRepository,
                                           final ObjectMapper objectMapper,
                                              final Assistant assistant,
                                              final ChatMemory chatMemory) {
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.level2SubcategoryRepository = level2SubcategoryRepository;
        this.objectMapper = objectMapper;
        this.assistant = assistant;
        this.chatMemory = chatMemory;
        this.init();
    }

    /**
     * Bootstraps data in the database by reading the categories.json file.
     */
    public void init() {
        try {
            this.bootstrap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping()
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{categoryId}/subcategories")
    public List<Subcategory> getSubcategories(@PathVariable Long categoryId) {
        return subcategoryRepository.findByCategoryId(categoryId);
    }

    @GetMapping("/{categoryId}/subcategories/{subcategoryId}/level2-subcategories")
    public List<Level2Subcategory> getLevel2Subcategories(@PathVariable Long subcategoryId) {
        return level2SubcategoryRepository.findBySubcategoryId(subcategoryId);
    }

    @PostMapping("/bootstrap")
    public void bootstrap() throws StreamReadException, DatabindException, IOException {
        this.cleanupDatabase();
        File  file;
        List<Category> categories;
        try {
            file = ResourceUtils.getFile("classpath:categories.json");
            categories = objectMapper.readValue(file, new TypeReference<List<Category>>() {});
        } catch (IOException e) {
            file = ResourceUtils.getFile("/categories.json");
            categories = objectMapper.readValue(file, new TypeReference<List<Category>>() {});
        }
        this.associateCategoriesSubcategoriesAndLevel2Subcategories(categories);
        categories.forEach(categoryRepository::save);
    }

    @PostMapping("/ai-item-categorization")
    public ItemInfoDto categorizeItem(@RequestBody ItemInfoDto itemInfoDto) {
        chatMemory.clear();
        final String answer =
            assistant.categorizeItem(PromptUtils.formatItemCategorizationUserPrompt(itemInfoDto));
        System.out.println(answer);
        ItemCategoryDto itemCategoryDto;
        try {
            final String jsonObjectAsAString = answer.replaceAll("```json", "").replaceAll("```", "");
            itemCategoryDto = objectMapper.readValue(jsonObjectAsAString, ItemCategoryDto.class);
            itemInfoDto.setCategory(itemCategoryDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        chatMemory.clear();
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


    /**
     * Cleans up the database by deleting all entries from the level 2 subcategory,
     * subcategory, and category repositories.
     */
    private void cleanupDatabase() {
        level2SubcategoryRepository.deleteAll();
        subcategoryRepository.deleteAll();
        categoryRepository.deleteAll();
    }
}
