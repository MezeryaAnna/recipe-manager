package com.anya.recipemanager.model.repository;

import com.anya.recipemanager.db.DatabaseConnection;
import com.anya.recipemanager.model.entity.Recipe;

import java.util.List;
import java.util.Optional;


public class JdbcRecipeRepository implements RecipeRepository {

    private final DatabaseConnection databaseConnection;

    public JdbcRecipeRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Recipe save(Recipe recipe) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Recipe> findById(long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Recipe> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Recipe> findByIngredient(String ingredientName) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteById(long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
