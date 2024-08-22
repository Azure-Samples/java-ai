package com.microsoft.azure.samples.aishop.ai_image_processing_service.model;

import java.util.Objects;

public class ItemInfo {
  
  private String label;
  private ItemCondition condition;
  private double price;
  private String description;
  
  public ItemInfo() {
  }
  
  public ItemInfo(String label, ItemCondition condition, double price, String description) {
    this.label = label;
    this.condition = condition;
    this.price = price;
    this.description = description;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ItemCondition getCondition() {
    return condition;
  }

  public void setCondition(ItemCondition condition) {
    this.condition = condition;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "ItemInfo{" +
      "label='" + label + '\'' +
      ", condition=" + condition +
      ", price=" + price +
      ", description='" + description + '\'' +
      '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, condition, price, description);
  }
}
