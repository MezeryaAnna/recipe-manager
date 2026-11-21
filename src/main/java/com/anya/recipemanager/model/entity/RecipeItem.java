package com.anya.recipemanager.model.entity;

public class RecipeItem {
    private final String ingredientName;
    private final double quantity;
    private final String unit;

    public RecipeItem(String ingredientName, double quantity, String unit) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }
}
