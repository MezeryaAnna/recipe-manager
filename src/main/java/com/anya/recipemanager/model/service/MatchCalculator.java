package com.anya.recipemanager.model.service;

import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.entity.RecipeItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatchCalculator {
    private static final double HUNDRED_PERCENT = 100.0;
    private static final double ZERO_PERCENT = 0.0;

    private final IngredientNameNormalizer normalizer;

    public MatchCalculator() {
        this(new IngredientNameNormalizer());
    }

    public MatchCalculator(IngredientNameNormalizer normalizer) {
        this.normalizer = normalizer;
    }

    public record MatchResult(double matchPercentage, List<String> missingIngredients) {
    }

    public MatchResult calculate(Recipe recipe, Collection<String> availableIngredients) {
        if (recipe == null || recipe.getItems().isEmpty()) {
            return new MatchResult(ZERO_PERCENT, List.of());
        }

        Set<String> normalizedAvailable = normalizeAvailable(availableIngredients);

        Map<String, String> requiredUniqueByNormalized = new LinkedHashMap<>();
        for (RecipeItem item : recipe.getItems()) {
            String rawName = item.getIngredientName();
            String normalized = normalizer.normalize(rawName);
            if (!normalized.isEmpty()) {
                requiredUniqueByNormalized.putIfAbsent(normalized, rawName);
            }
        }

        int totalRequired = requiredUniqueByNormalized.size();
        if (totalRequired == 0) {
            return new MatchResult(ZERO_PERCENT, List.of());
        }

        int matchedCount = 0;
        List<String> missing = new ArrayList<>();

        for (Map.Entry<String, String> entry : requiredUniqueByNormalized.entrySet()) {
            if (normalizedAvailable.contains(entry.getKey())) {
                matchedCount++;
            } else {
                missing.add(entry.getValue());
            }
        }

        double percentage = (matchedCount * HUNDRED_PERCENT) / totalRequired;
        return new MatchResult(percentage, List.copyOf(missing));
    }

    private Set<String> normalizeAvailable(Collection<String> availableIngredients) {
        Set<String> result = new HashSet<>();
        if (availableIngredients == null) {
            return result;
        }
        for (String ingredient : availableIngredients) {
            String normalized = normalizer.normalize(ingredient);
            if (!normalized.isEmpty()) {
                result.add(normalized);
            }
        }
        return result;
    }
}
