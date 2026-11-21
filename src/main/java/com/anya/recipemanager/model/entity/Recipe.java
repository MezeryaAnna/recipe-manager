package com.anya.recipemanager.model.entity;
import java.util.Collections;
import java.util.List;

public class Recipe {
    private final String title;
    private final List<RecipeItem> items;

    private Recipe(Builder builder) {
        this.title = builder.title;
        this.items = builder.items != null ? List.copyOf(builder.items) : Collections.emptyList();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public List<RecipeItem> getItems() {
        return items;
    }

    public static class Builder {
        private String title;
        private List<RecipeItem> items;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder items(List<RecipeItem> items) {
            this.items = items;
            return this;
        }

        public Recipe build() {
            return new Recipe(this);
        }
    }
}
