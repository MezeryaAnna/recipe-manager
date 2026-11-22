package com.anya.recipemanager.model.service;

import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.entity.RecipeItem;
import com.anya.recipemanager.model.repository.RecipeRepository;
import com.anya.recipemanager.model.strategy.ByFewestMissingStrategy;
import com.anya.recipemanager.model.strategy.ByPercentStrategy;
import com.anya.recipemanager.model.strategy.RankingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecipeServiceTest {
    private InMemoryRecipeRepository repository;
    private RecipeService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRecipeRepository();
        service = new RecipeService(repository, new IngredientNameNormalizer(), new MatchCalculator());
    }

    @Test
    void shouldCreateFindUpdateAndDeleteRecipe() {
        Recipe created = service.createRecipe(Recipe.builder()
                .title("Борщ")
                .instruction("Варити")
                .items(List.of(new RecipeItem("буряк", 2, "шт")))
                .build());

        assertTrue(service.getRecipeById(created.getId()).isPresent());
        assertEquals(1, service.getAllRecipes().size());

        Recipe updated = service.updateRecipe(created.getId(), Recipe.builder()
                .title("Червоний борщ")
                .instruction("Варити 40 хв")
                .items(List.of(new RecipeItem("буряк", 3, "шт")))
                .build());

        assertEquals("Червоний борщ", updated.getTitle());
        assertEquals("Варити 40 хв", updated.getInstruction());

        service.deleteRecipe(created.getId());
        assertTrue(service.getRecipeById(created.getId()).isEmpty());
    }

    @Test
    void shouldRejectBlankRecipeTitle() {
        Recipe invalid = Recipe.builder()
                .title("   ")
                .instruction("Кроки")
                .items(List.of(new RecipeItem("сіль", 1, "г")))
                .build();

        assertThrows(IllegalArgumentException.class, () -> service.createRecipe(invalid));
    }

    @Test
    void shouldSearchBySingleAndMultipleIngredientsCaseInsensitively() {
        service.createRecipe(Recipe.builder()
                .title("Курячий суп")
                .items(List.of(
                        new RecipeItem("Курка", 300, "г"),
                        new RecipeItem("морква", 1, "шт")
                ))
                .build());

        service.createRecipe(Recipe.builder()
                .title("Омлет")
                .items(List.of(
                        new RecipeItem("яйця", 2, "шт"),
                        new RecipeItem("молоко", 100, "мл")
                ))
                .build());

        List<Recipe> byChicken = service.searchByIngredient("  КУРКА ");
        assertEquals(1, byChicken.size());
        assertEquals("Курячий суп", byChicken.get(0).getTitle());

        List<Recipe> multi = service.searchByIngredients(List.of("курка", "яйця"));
        assertEquals(2, multi.size());
    }

    @Test
    void shouldRankMatchingRecipesWithBothStrategies() {
        service.createRecipe(Recipe.builder()
                .title("Страва А")
                .items(List.of(
                        new RecipeItem("яйця", 2, "шт"),
                        new RecipeItem("сир", 50, "г")
                ))
                .build());

        service.createRecipe(Recipe.builder()
                .title("Страва Б")
                .items(List.of(
                        new RecipeItem("яйця", 2, "шт"),
                        new RecipeItem("сир", 50, "г"),
                        new RecipeItem("помідор", 1, "шт"),
                        new RecipeItem("огірок", 1, "шт")
                ))
                .build());

        List<RankingStrategy.RankedRecipe> byPercent =
                service.findMatchingRecipes(List.of("яйця", "сир"), new ByPercentStrategy());
        assertEquals("Страва А", byPercent.get(0).recipe().getTitle());
        assertEquals(100.0, byPercent.get(0).matchResult().matchPercentage(), 0.01);

        List<RankingStrategy.RankedRecipe> byFewestMissing =
                service.findMatchingRecipes(List.of("яйця", "сир"), new ByFewestMissingStrategy());
        assertEquals("Страва А", byFewestMissing.get(0).recipe().getTitle());
        assertEquals(0, byFewestMissing.get(0).matchResult().missingIngredients().size());
    }

    private static final class InMemoryRecipeRepository implements RecipeRepository {
        private final Map<Long, Recipe> storage = new LinkedHashMap<>();
        private final AtomicLong seq = new AtomicLong(1);

        @Override
        public Recipe save(Recipe recipe) {
            long id = recipe.getId() != null ? recipe.getId() : seq.getAndIncrement();
            Recipe copy = Recipe.builder()
                    .id(id)
                    .title(recipe.getTitle())
                    .instruction(recipe.getInstruction())
                    .items(recipe.getItems())
                    .build();
            storage.put(id, copy);
            return copy;
        }

        @Override
        public Optional<Recipe> findById(long id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Recipe> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public List<Recipe> findByIngredient(String ingredientName) {
            String target = ingredientName == null ? "" : ingredientName.trim().toLowerCase(Locale.ROOT);
            return storage.values().stream()
                    .filter(r -> r.getItems().stream()
                            .anyMatch(item -> item.getIngredientName() != null
                                    && item.getIngredientName().trim().toLowerCase(Locale.ROOT).equals(target)))
                    .toList();
        }

        @Override
        public void deleteById(long id) {
            storage.remove(id);
        }
    }

}
