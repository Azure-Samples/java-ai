package com.microsoft.azure.samples.aishop.item_category_service.repository;


import java.util.List;

import org.springframework.data.repository.Repository;

import com.microsoft.azure.samples.aishop.item_category_service.model.Subcategory;

public interface SubcategoryRepository extends Repository<Subcategory, Long> {

    Subcategory save(Subcategory subcategory);

    Subcategory findByName(String name);

    Subcategory findById(Long id);

    List<Subcategory> findByCategoryId(Long categoryId);

    void delete(Subcategory subcategory);

    void deleteAll();
}
