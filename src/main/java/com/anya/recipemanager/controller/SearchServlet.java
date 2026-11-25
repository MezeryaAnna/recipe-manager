package com.anya.recipemanager.controller;

import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.service.RecipeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        RecipeService service = (RecipeService) getServletContext()
                .getAttribute(AppContextListener.RECIPE_SERVICE_ATTR);
        if (service == null) {
            throw new IllegalStateException("RecipeService is not initialized in ServletContext");
        }

        String rawQuery = req.getParameter("ingredients");
        if (rawQuery == null || rawQuery.isBlank()) {
            rawQuery = req.getParameter("q");
        }

        List<Recipe> results = List.of();
        if (rawQuery != null && !rawQuery.isBlank()) {
            List<String> tokens = Arrays.stream(rawQuery.split("[,;\\r\\n]+"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            results = tokens.size() == 1
                    ? service.searchByIngredient(tokens.get(0))
                    : service.searchByIngredients(tokens);
        }

        req.setAttribute("query", rawQuery != null ? rawQuery : "");
        req.setAttribute("results", results);
        req.getRequestDispatcher("/WEB-INF/view/search.jsp").forward(req, resp);
    }

}
