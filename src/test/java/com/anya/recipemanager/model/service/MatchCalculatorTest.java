package com.anya.recipemanager.model.service;
import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.entity.RecipeItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatchCalculatorTest {
    private final MatchCalculator calculator = new MatchCalculator();

    @Test
    void shouldReturnZeroPercentWhenNoIngredientsMatch() {
        Recipe recipe = Recipe.builder()
                .title("Омлет")
                .items(List.of(
                        new RecipeItem("яйця", 2, "шт"),
                        new RecipeItem("молоко", 100, "мл")
                ))
                .build();

        MatchCalculator.MatchResult result = calculator.calculate(recipe, List.of("хліб", "сир"));

        assertEquals(0.0, result.matchPercentage(), 0.01);
        assertEquals(List.of("яйця", "молоко"), result.missingIngredients());
    }

    @Test
    void shouldReturnHundredPercentWhenAllIngredientsAvailable() {
        Recipe recipe = Recipe.builder()
                .title("Тости")
                .items(List.of(
                        new RecipeItem("хліб", 2, "шт"),
                        new RecipeItem("масло", 20, "г")
                ))
                .build();

        MatchCalculator.MatchResult result = calculator.calculate(recipe, Set.of("ХЛІБ", "масло"));

        assertEquals(100.0, result.matchPercentage(), 0.01);
        assertTrue(result.missingIngredients().isEmpty());
    }

    @Test
    void shouldHandleEmptyRecipeGracefully() {
        Recipe emptyRecipe = Recipe.builder()
                .title("Порожній рецепт")
                .items(List.of())
                .build();

        MatchCalculator.MatchResult result = calculator.calculate(emptyRecipe, List.of("яйця"));

        assertEquals(0.0, result.matchPercentage(), 0.01);
        assertTrue(result.missingIngredients().isEmpty());
    }

    @Test
    void shouldIgnoreExtraIngredientsInFridge() {
        Recipe recipe = Recipe.builder()
                .title("Салат")
                .items(List.of(
                        new RecipeItem("огірок", 1, "шт"),
                        new RecipeItem("помідор", 1, "шт")
                ))
                .build();

        MatchCalculator.MatchResult result = calculator.calculate(
                recipe,
                List.of("огірок", "сметана", "зелень", "сіль")
        );

        assertEquals(50.0, result.matchPercentage(), 0.01);
        assertEquals(List.of("помідор"), result.missingIngredients());
    }

}
