package com.microsoft.azure.samples.aishop.item_category_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Level2Subcategory {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "subcategory_id", nullable = false)
    private Subcategory subcategory;

    public Level2Subcategory() {
    }

    public Level2Subcategory(final String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setSubcategory(final Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    @Override
    public String toString() {
        return "Subcategory - Level 2 [id=" + id + ", name=" + name + "]";
    }
}
