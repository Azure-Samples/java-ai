package com.microsoft.azure.samples.aishop.item_category_service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Subcategory {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "subcategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Level2Subcategory> level2Subcategories;

    public Subcategory() {
    }

    public Subcategory(final String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public List<Level2Subcategory> getLevel2Subcategories() {
        return level2Subcategories;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }

    public void setLevel2Subcategories(final List<Level2Subcategory> level2Subcategories) {
        this.level2Subcategories = level2Subcategories;
    }

    @Override
    public String toString() {
        return "Subcategory [id=" + id + ", name=" + name + ", level2Subcategories=" + level2Subcategories + "]";
    }
}
