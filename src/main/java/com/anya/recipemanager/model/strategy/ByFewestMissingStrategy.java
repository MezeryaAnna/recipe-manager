package com.anya.recipemanager.model.strategy;
import java.util.Comparator;

public class ByFewestMissingStrategy implements RankingStrategy {
    @Override
    public Comparator<RankedRecipe> comparator() {
        return Comparator
                .comparingInt((RankedRecipe r) -> r.matchResult().missingIngredients().size())
                .thenComparing(Comparator.comparingDouble((RankedRecipe r) -> r.matchResult().matchPercentage()).reversed())
                .thenComparing(r -> r.recipe().getTitle(), String.CASE_INSENSITIVE_ORDER);
    }
}
