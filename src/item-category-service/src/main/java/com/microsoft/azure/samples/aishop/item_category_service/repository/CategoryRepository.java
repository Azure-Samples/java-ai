package com.microsoft.azure.samples.aishop.item_category_service.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.microsoft.azure.samples.aishop.item_category_service.model.Category;

public interface CategoryRepository extends Repository<Category, Long> {

    Category save(Category category);

    Category findByName(String name);

    Category findById(Long id);

    List<Category> findAll();

    List<Category> findAllByOrderByNameAsc();

    void delete(Category category);   
}