package com.microsoft.azure.samples.java_ai.common.dto;

import java.util.Objects;

public class ItemInfoDto {
  
  private String label;
  private String brand;
  private String model;
  private ItemCondition condition;
  private double price;
  private String description;

  public ItemInfoDto() {
  }
  
  public ItemInfoDto(final String label, final String brand, final String model, final ItemCondition condition, final double price, final String description) {
    this.label = label;
    this.brand = brand;
    this.model = model;
    this.condition = condition;
    this.price = price;
    this.description = description;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(final String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(final String model) {
    this.model = model;
  }

  public ItemCondition getCondition() {
    return condition;
  }

  public void setCondition(final ItemCondition condition) {
    this.condition = condition;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(final double price) {
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "ItemInfo{" +
        "label='" + label + '\'' +
        ", brand='" + brand + '\'' +
        ", model='" + model + '\'' +
        ", condition=" + condition +
        ", price=" + price +
        ", description='" + description + '\'' +
        '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, brand, model, condition, price, description);
  }
}
