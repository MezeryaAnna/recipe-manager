package com.anya.recipemanager.model.repository;

import com.anya.recipemanager.db.DatabaseConnection;
import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.entity.RecipeItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class JdbcRecipeRepositoryTest {

    private DatabaseConnection databaseConnection;
    private RecipeRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        databaseConnection = new DatabaseConnection();
        repository = new JdbcRecipeRepository(databaseConnection);
        cleanTables();
    }

    private void cleanTables() throws SQLException {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "TRUNCATE TABLE recipe_ingredient, ingredient, recipe RESTART IDENTITY CASCADE")) {
            stmt.executeUpdate();
        }
    }

    @Test
    void shouldSaveAndFindRecipeByIdAndCaseInsensitiveIngredient() {
        Recipe recipe = Recipe.builder()
                .title("Курячий суп")
                .instruction("Зварити бульйон і додати овочі")
                .items(List.of(
                        new RecipeItem("Курка", new BigDecimal("400.00"), "г"),
                        new RecipeItem("морква", new BigDecimal("1.00"), "шт")
                ))
                .build();

        Recipe saved = repository.save(recipe);
        Optional<Recipe> foundById = repository.findById(saved.getId());
        List<Recipe> foundByLowerCase = repository.findByIngredient("курка");

        assertTrue(foundById.isPresent());
        assertEquals("Курячий суп", foundById.get().getTitle());
        assertEquals(2, foundById.get().getItems().size());
        assertEquals(1, foundByLowerCase.size());
        assertEquals(saved.getId(), foundByLowerCase.get(0).getId());
    }

    @Test
    void shouldRollbackTransactionWhenSavingItemFails() throws SQLException {
        Recipe brokenRecipe = Recipe.builder()
                .title("Зламаний рецепт")
                .instruction("Не повинен зберегтися")
                .items(List.of(
                        new RecipeItem("картопля", new BigDecimal("200.00"), "г"),
                        new RecipeItem("сіль", (BigDecimal) null, "дрібка")
                ))
                .build();

        assertThrows(RuntimeException.class, () -> repository.save(brokenRecipe));

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM recipe");
             ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(0L, rs.getLong(1));
        }
    }

    @Test
    void shouldProtectAgainstSqlInjectionInSearch() throws SQLException {
        String payload = "'; DROP TABLE recipe; --";
        List<Recipe> result = repository.findByIngredient(payload);
        assertTrue(result.isEmpty());

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM recipe");
             ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(0L, rs.getLong(1));
        }
    }
}
