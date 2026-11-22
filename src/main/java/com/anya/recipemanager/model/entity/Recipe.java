package com.anya.recipemanager.model.entity;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Recipe {
    private final String title;
    private final List<RecipeItem> items;
    private final String instruction;
    private final Long id;

    private Recipe(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.instruction = builder.instruction;
        this.items = builder.items != null ? List.copyOf(builder.items) : Collections.emptyList();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getInstruction() {
        return instruction;
    }

    public List<RecipeItem> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recipe recipe)) {
            return false;
        }
        return Objects.equals(id, recipe.id)
                && Objects.equals(title, recipe.title)
                && Objects.equals(instruction, recipe.instruction)
                && Objects.equals(items, recipe.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, instruction, items);
    }

    public static class Builder {
        private Long id;
        private String title;
        private String instruction;
        private List<RecipeItem> items;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder instruction(String instruction) {
            this.instruction = instruction;
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
