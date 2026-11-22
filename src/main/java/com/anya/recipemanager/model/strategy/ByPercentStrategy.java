package com.anya.recipemanager.model.strategy;
import java.util.Comparator;

public class ByPercentStrategy implements RankingStrategy {
    @Override
    public Comparator<RankedRecipe> comparator() {
        return Comparator
                .comparingDouble((RankedRecipe r) -> r.matchResult().matchPercentage())
                .reversed()
                .thenComparingInt(r -> r.matchResult().missingIngredients().size())
                .thenComparing(r -> r.recipe().getTitle(), String.CASE_INSENSITIVE_ORDER);
    }
}
