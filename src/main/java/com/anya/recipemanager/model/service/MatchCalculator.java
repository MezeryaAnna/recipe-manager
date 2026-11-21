package com.anya.recipemanager.model.service;

import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.entity.RecipeItem;

import java.util.Collection;
import java.util.List;

public class MatchCalculator {
    public record MatchResult(double matchPercentage, List<String> missingIngredients) {
    }

    public MatchResult calculate(Recipe recipe, Collection<String> availableIngredients) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
