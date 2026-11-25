package com.anya.recipemanager.controller;

import com.anya.recipemanager.db.DatabaseConnection;
import com.anya.recipemanager.model.repository.JdbcRecipeRepository;
import com.anya.recipemanager.model.repository.RecipeRepository;
import com.anya.recipemanager.model.service.IngredientNameNormalizer;
import com.anya.recipemanager.model.service.MatchCalculator;
import com.anya.recipemanager.model.service.RecipeService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    public static final String RECIPE_SERVICE_ATTR = "recipeService";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        DatabaseConnection dbConnection = new DatabaseConnection();
        RecipeRepository repository = new JdbcRecipeRepository(dbConnection);
        RecipeService service = new RecipeService(
                repository,
                new IngredientNameNormalizer(),
                new MatchCalculator()
        );
        ctx.setAttribute(RECIPE_SERVICE_ATTR, service);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute(RECIPE_SERVICE_ATTR);
    }
}