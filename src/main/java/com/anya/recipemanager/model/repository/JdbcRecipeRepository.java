package com.anya.recipemanager.model.repository;

import com.anya.recipemanager.db.DatabaseConnection;
import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.entity.RecipeItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


public class JdbcRecipeRepository implements RecipeRepository {

    private static final String INSERT_RECIPE_SQL =
            "INSERT INTO recipe (title, instruction) VALUES (?, ?)";

    private static final String UPSERT_INGREDIENT_SQL =
            "INSERT INTO ingredient (name) VALUES (?) ON CONFLICT (name) DO NOTHING";

    private static final String SELECT_INGREDIENT_ID_SQL =
            "SELECT id FROM ingredient WHERE LOWER(name) = LOWER(?)";

    private static final String INSERT_RECIPE_INGREDIENT_SQL =
            "INSERT INTO recipe_ingredient (recipe_id, ingredient_id, amount, unit) VALUES (?, ?, ?, ?)";

    private static final String SELECT_RECIPE_BY_ID_SQL =
            "SELECT r.id, r.title, r.instruction, i.name AS ingredient_name, ri.amount, ri.unit " +
                    "FROM recipe r " +
                    "LEFT JOIN recipe_ingredient ri ON r.id = ri.recipe_id " +
                    "LEFT JOIN ingredient i ON ri.ingredient_id = i.id " +
                    "WHERE r.id = ?";

    private static final String SELECT_ALL_RECIPES_SQL =
            "SELECT r.id, r.title, r.instruction, i.name AS ingredient_name, ri.amount, ri.unit " +
                    "FROM recipe r " +
                    "LEFT JOIN recipe_ingredient ri ON r.id = ri.recipe_id " +
                    "LEFT JOIN ingredient i ON ri.ingredient_id = i.id " +
                    "ORDER BY r.id";

    private static final String SELECT_RECIPES_BY_INGREDIENT_SQL =
            "SELECT DISTINCT r.id " +
                    "FROM recipe r " +
                    "JOIN recipe_ingredient ri ON r.id = ri.recipe_id " +
                    "JOIN ingredient i ON ri.ingredient_id = i.id " +
                    "WHERE LOWER(i.name) = LOWER(?) " +
                    "ORDER BY r.id";

    private static final String DELETE_RECIPE_BY_ID_SQL =
            "DELETE FROM recipe WHERE id = ?";

    private final DatabaseConnection databaseConnection;

    public JdbcRecipeRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Recipe save(Recipe recipe) {
        try (Connection conn = databaseConnection.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                long recipeId = insertRecipe(conn, recipe);
                for (RecipeItem item : recipe.getItems()) {
                    long ingredientId = getOrCreateIngredientId(conn, item.getIngredientName());
                    insertRecipeIngredient(conn, recipeId, ingredientId, item);
                }
                conn.commit();
                return findById(conn, recipeId)
                        .orElseThrow(() -> new IllegalStateException("Saved recipe not found: " + recipeId));
            } catch (SQLException | RuntimeException ex) {
                rollbackQuietly(conn);
                throw new RuntimeException("Failed to save recipe, transaction rolled back", ex);
            } finally {
                restoreAutoCommitQuietly(conn, previousAutoCommit);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Database connection error while saving recipe", ex);
        }
    }

    @Override
    public Optional<Recipe> findById(long id) {
        try (Connection conn = databaseConnection.getConnection()) {
            return findById(conn, id);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch recipe by id: " + id, ex);
        }
    }

    @Override
    public List<Recipe> findAll() {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_RECIPES_SQL);
             ResultSet rs = stmt.executeQuery()) {
            return new ArrayList<>(mapRecipes(rs).values());
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch all recipes", ex);
        }
    }

    @Override
    public List<Recipe> findByIngredient(String ingredientName) {
        String normalizedQuery = ingredientName == null ? "" : ingredientName.trim().toLowerCase(Locale.ROOT);
        List<Long> matchingIds = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_RECIPES_BY_INGREDIENT_SQL)) {
            stmt.setString(1, normalizedQuery);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matchingIds.add(rs.getLong("id"));
                }
            }
            List<Recipe> result = new ArrayList<>();
            for (Long recipeId : matchingIds) {
                findById(conn, recipeId).ifPresent(result::add);
            }
            return result;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to search recipes by ingredient", ex);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_RECIPE_BY_ID_SQL)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete recipe by id: " + id, ex);
        }
    }

    private long insertRecipe(Connection conn, Recipe recipe) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_RECIPE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, recipe.getTitle());
            stmt.setString(2, recipe.getInstruction());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("No generated key returned for recipe");
            }
        }
    }

    private long getOrCreateIngredientId(Connection conn, String rawName) throws SQLException {
        if (rawName == null || rawName.isBlank()) {
            throw new IllegalArgumentException("Ingredient name must not be blank");
        }
        String normalizedName = rawName.trim().toLowerCase(Locale.ROOT);

        try (PreparedStatement insertStmt = conn.prepareStatement(UPSERT_INGREDIENT_SQL)) {
            insertStmt.setString(1, normalizedName);
            insertStmt.executeUpdate();
        }

        try (PreparedStatement selectStmt = conn.prepareStatement(SELECT_INGREDIENT_ID_SQL)) {
            selectStmt.setString(1, normalizedName);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                throw new SQLException("Ingredient id not found after upsert: " + normalizedName);
            }
        }
    }

    private void insertRecipeIngredient(Connection conn, long recipeId, long ingredientId, RecipeItem item)
            throws SQLException {
        if (item.getAmount() == null || item.getUnit() == null || item.getUnit().isBlank()) {
            throw new SQLException("Invalid recipe item amount or unit");
        }
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_RECIPE_INGREDIENT_SQL)) {
            stmt.setLong(1, recipeId);
            stmt.setLong(2, ingredientId);
            stmt.setBigDecimal(3, item.getAmount());
            stmt.setString(4, item.getUnit().trim());
            stmt.executeUpdate();
        }
    }

    private Optional<Recipe> findById(Connection conn, long id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_RECIPE_BY_ID_SQL)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                Map<Long, Recipe> mapped = mapRecipes(rs);
                return Optional.ofNullable(mapped.get(id));
            }
        }
    }

    private Map<Long, Recipe> mapRecipes(ResultSet rs) throws SQLException {
        Map<Long, RecipeBuilderHolder> holders = new LinkedHashMap<>();
        while (rs.next()) {
            long recipeId = rs.getLong("id");
            RecipeBuilderHolder holder = holders.computeIfAbsent(recipeId, id -> {
                try {
                    return new RecipeBuilderHolder(
                            id,
                            rs.getString("title"),
                            rs.getString("instruction")
                    );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            String ingredientName = rs.getString("ingredient_name");
            if (ingredientName != null) {
                holder.items.add(new RecipeItem(
                        ingredientName,
                        rs.getBigDecimal("amount"),
                        rs.getString("unit")
                ));
            }
        }

        Map<Long, Recipe> result = new LinkedHashMap<>();
        for (Map.Entry<Long, RecipeBuilderHolder> entry : holders.entrySet()) {
            RecipeBuilderHolder h = entry.getValue();
            result.put(entry.getKey(), Recipe.builder()
                    .id(h.id)
                    .title(h.title)
                    .instruction(h.instruction)
                    .items(h.items)
                    .build());
        }
        return result;
    }

    private void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void restoreAutoCommitQuietly(Connection conn, boolean previousAutoCommit) {
        try {
            conn.setAutoCommit(previousAutoCommit);
        } catch (SQLException ignored) {
        }
    }

    private static final class RecipeBuilderHolder {
        private final long id;
        private final String title;
        private final String instruction;
        private final List<RecipeItem> items = new ArrayList<>();

        private RecipeBuilderHolder(long id, String title, String instruction) {
            this.id = id;
            this.title = title;
            this.instruction = instruction;
        }
    }
}
