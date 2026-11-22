package com.anya.recipemanager.model.service;

import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.repository.RecipeRepository;
import com.anya.recipemanager.model.strategy.ByPercentStrategy;
import com.anya.recipemanager.model.strategy.RankingStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        validateRecipe(recipe);
        Recipe toSave = Recipe.builder()
                .title(recipe.getTitle().trim())
                .instruction(recipe.getInstruction())
                .items(recipe.getItems())
                .build();
        return repository.save(toSave);
    }

    public List<Recipe> getAllRecipes() {
        return repository.findAll();
    }

    public Optional<Recipe> getRecipeById(long id) {
        return repository.findById(id);
    }

    public List<Recipe> searchByIngredient(String ingredientName) {
        String normalized = normalizer.normalize(ingredientName);
        if (normalized.isEmpty()) {
            return List.of();
        }
        return repository.findByIngredient(normalized);
    }

    public Recipe updateRecipe(long id, Recipe updatedRecipe) {
        validateRecipe(updatedRecipe);
        repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + id));
        Recipe toSave = Recipe.builder()
                .id(id)
                .title(updatedRecipe.getTitle().trim())
                .instruction(updatedRecipe.getInstruction())
                .items(updatedRecipe.getItems())
                .build();
        return repository.save(toSave);
    }

    public void deleteRecipe(long id) {
        repository.deleteById(id);
    }

    public List<Recipe> searchByIngredients(Collection<String> ingredientNames) {
        if (ingredientNames == null || ingredientNames.isEmpty()) {
            return List.of();
        }
        Map<Long, Recipe> uniqueRecipes = new LinkedHashMap<>();
        for (String rawName : ingredientNames) {
            for (Recipe recipe : searchByIngredient(rawName)) {
                if (recipe.getId() != null) {
                    uniqueRecipes.putIfAbsent(recipe.getId(), recipe);
                }
            }
        }
        return new ArrayList<>(uniqueRecipes.values());
    }

    public List<RankingStrategy.RankedRecipe> findMatchingRecipes(Collection<String> availableIngredients,
                                                                  RankingStrategy strategy) {
        RankingStrategy activeStrategy = strategy != null ? strategy : new ByPercentStrategy();
        List<Recipe> allRecipes = repository.findAll();
        List<RankingStrategy.RankedRecipe> candidates = new ArrayList<>(allRecipes.size());
        for (Recipe recipe : allRecipes) {
            MatchCalculator.MatchResult matchResult = matchCalculator.calculate(recipe, availableIngredients);
            candidates.add(new RankingStrategy.RankedRecipe(recipe, matchResult));
        }
        return activeStrategy.rank(candidates);
    }

    public List<RankingStrategy.RankedRecipe> findMatchingRecipes(Collection<String> availableIngredients) {
        return findMatchingRecipes(availableIngredients, new ByPercentStrategy());
    }

    private void validateRecipe(Recipe recipe) {
        if (recipe == null || recipe.getTitle() == null || recipe.getTitle().isBlank()) {
            throw new IllegalArgumentException("Recipe title must not be blank");
        }
    }
}
