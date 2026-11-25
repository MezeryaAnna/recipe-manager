package com.anya.recipemanager.controller;

import com.anya.recipemanager.model.service.RecipeService;
import com.anya.recipemanager.model.strategy.ByFewestMissingStrategy;
import com.anya.recipemanager.model.strategy.ByPercentStrategy;
import com.anya.recipemanager.model.strategy.RankingStrategy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/fridge-match")
public class FridgeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleMatch(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        handleMatch(req, resp);
    }

    private void handleMatch(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        RecipeService service = (RecipeService) getServletContext()
                .getAttribute(AppContextListener.RECIPE_SERVICE_ATTR);
        if (service == null) {
            throw new IllegalStateException("RecipeService is not initialized in ServletContext");
        }

        String rawIngredients = req.getParameter("ingredients");
        String strategyParam = req.getParameter("strategy");
        boolean fewestMissing = "fewest-missing".equalsIgnoreCase(strategyParam);
        RankingStrategy strategy = fewestMissing
                ? new ByFewestMissingStrategy()
                : new ByPercentStrategy();

        List<String> available = (rawIngredients == null || rawIngredients.isBlank())
                ? List.of()
                : Arrays.stream(rawIngredients.split("[,;\\r\\n]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        List<RankingStrategy.RankedRecipe> matches = available.isEmpty()
                ? List.of()
                : service.findMatchingRecipes(available, strategy);

        req.setAttribute("ingredientsInput", rawIngredients != null ? rawIngredients : "");
        req.setAttribute("strategy", fewestMissing ? "fewest-missing" : "percent");
        req.setAttribute("matches", matches);
        req.getRequestDispatcher("/WEB-INF/view/fridge-match.jsp").forward(req, resp);
    }
}
