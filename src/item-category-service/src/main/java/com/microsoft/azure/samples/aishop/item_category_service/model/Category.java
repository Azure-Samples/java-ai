package com.microsoft.azure.samples.aishop.item_category_service.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subcategory> subcategories;

    public Category() {
    }

    public Category(final String name, final List<Subcategory> subcategories) {
        this.name = name;
        this.subcategories = subcategories;
    }
    
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setSubcategories(final List<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + ", subcategories=" + subcategories + "]";
    }
}
