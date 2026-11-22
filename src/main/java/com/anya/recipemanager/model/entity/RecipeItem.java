package com.anya.recipemanager.model.entity;


import java.math.BigDecimal;
import java.util.Objects;

public class RecipeItem {
    private final String ingredientName;
    private final BigDecimal amount;
    private final String unit;

    public RecipeItem(String ingredientName, BigDecimal amount, String unit) {
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.unit = unit;
    }

    public RecipeItem(String ingredientName, double amount, String unit) {
        this(ingredientName, BigDecimal.valueOf(amount), unit);
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public double getQuantity() {
        return amount != null ? amount.doubleValue() : 0.0;
    }


    public String getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecipeItem that)) {
            return false;
        }
        return Objects.equals(ingredientName, that.ingredientName)
                && compareBigDecimal(amount, that.amount)
                && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredientName, amount != null ? amount.stripTrailingZeros() : null, unit);
    }

    private boolean compareBigDecimal(BigDecimal first, BigDecimal second) {
        if (first == null || second == null) {
            return Objects.equals(first, second);
        }
        return first.compareTo(second) == 0;
    }
}
