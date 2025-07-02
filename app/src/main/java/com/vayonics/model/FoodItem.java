package com.vayonics.model;

public class FoodItem {
    private String name;
    private String description;
    private int price;

    public FoodItem() {} // required for Firebase

    public FoodItem(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
}

