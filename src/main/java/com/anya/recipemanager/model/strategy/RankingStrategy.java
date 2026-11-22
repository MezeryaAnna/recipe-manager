package com.anya.recipemanager.model.strategy;

import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.service.MatchCalculator;

import java.util.Comparator;
import java.util.List;

public interface RankingStrategy {
    record RankedRecipe(Recipe recipe, MatchCalculator.MatchResult matchResult) {
    }

    Comparator<RankedRecipe> comparator();

    default List<RankedRecipe> rank(List<RankedRecipe> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        return candidates.stream()
                .sorted(comparator())
                .toList();
    }
}
