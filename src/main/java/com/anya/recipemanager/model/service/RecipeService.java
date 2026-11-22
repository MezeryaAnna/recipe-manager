package com.anya.recipemanager.model.service;

import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.repository.RecipeRepository;
import com.anya.recipemanager.model.strategy.ByPercentStrategy;
import com.anya.recipemanager.model.strategy.RankingStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RecipeService {
    private final RecipeRepository repository;
    private final IngredientNameNormalizer normalizer;
    private final MatchCalculator matchCalculator;

    public RecipeService(RecipeRepository repository) {
        this(repository, new IngredientNameNormalizer(), new MatchCalculator());
    }

    public RecipeService(RecipeRepository repository,
                         IngredientNameNormalizer normalizer,
                         MatchCalculator matchCalculator) {
        this.repository = repository;
        this.normalizer = normalizer;
        this.matchCalculator = matchCalculator;
    }

    public Recipe createRecipe(Recipe recipe) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Recipe> getAllRecipes() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Optional<Recipe> getRecipeById(long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Recipe> searchByIngredient(String ingredientName) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Recipe updateRecipe(long id, Recipe updatedRecipe) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void deleteRecipe(long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Recipe> searchByIngredients(Collection<String> ingredientNames) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<RankingStrategy.RankedRecipe> findMatchingRecipes(Collection<String> availableIngredients,
                                                                  RankingStrategy strategy) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<RankingStrategy.RankedRecipe> findMatchingRecipes(Collection<String> availableIngredients) {
        return findMatchingRecipes(availableIngredients, new ByPercentStrategy());
    }
}
