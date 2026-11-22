package com.anya.recipemanager.model.repository;
import com.anya.recipemanager.model.entity.Recipe;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(long id);

    List<Recipe> findAll();

    List<Recipe> findByIngredient(String ingredientName);

    void deleteById(long id);

}
