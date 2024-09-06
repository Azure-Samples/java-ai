package com.microsoft.azure.samples.aishop.item_category_service.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.microsoft.azure.samples.aishop.item_category_service.model.Level2Subcategory;

public interface Level2SubcategoryRepository extends Repository<Level2Subcategory, Long> {

    Level2Subcategory save(Level2Subcategory level2Subcategory);

    Level2Subcategory findByName(String name);

    Level2Subcategory findById(Long id);

    List<Level2Subcategory> findBySubcategoryId(Long subcategoryId);

    void delete(Level2Subcategory level2Subcategory);
    
}
