package com.anya.recipemanager.controller;

import com.anya.recipemanager.model.entity.Recipe;
import com.anya.recipemanager.model.entity.RecipeItem;
import com.anya.recipemanager.model.service.RecipeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@WebServlet(urlPatterns = {
        "/recipes",
        "/recipe",
        "/recipe/new",
        "/recipe/edit",
        "/recipe/save",
        "/recipe/delete"
})
public class RecipeServlet extends HttpServlet {

    private RecipeService recipeService() {
        RecipeService service = (RecipeService) getServletContext()
                .getAttribute(AppContextListener.RECIPE_SERVICE_ATTR);
        if (service == null) {
            throw new IllegalStateException("RecipeService is not initialized in ServletContext");
        }
        return service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        RecipeService service = recipeService();

        switch (path) {
            case "/recipes" -> {
                req.setAttribute("recipes", service.getAllRecipes());
                req.getRequestDispatcher("/WEB-INF/view/recipe-list.jsp").forward(req, resp);
            }
            case "/recipe" -> {
                Long id = parseLongOrNull(req.getParameter("id"));
                if (id == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid recipe id");
                    return;
                }
                Optional<Recipe> recipeOpt = service.getRecipeById(id);
                if (recipeOpt.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Recipe not found");
                    return;
                }
                req.setAttribute("recipe", recipeOpt.get());
                req.getRequestDispatcher("/WEB-INF/view/recipe-details.jsp").forward(req, resp);
            }
            case "/recipe/new" -> {
                req.setAttribute("recipe", Recipe.builder().items(List.of()).build());
                req.setAttribute("mode", "create");
                req.getRequestDispatcher("/WEB-INF/view/recipe-form.jsp").forward(req, resp);
            }
            case "/recipe/edit" -> {
                Long id = parseLongOrNull(req.getParameter("id"));
                if (id == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid recipe id");
                    return;
                }
                Optional<Recipe> recipeOpt = service.getRecipeById(id);
                if (recipeOpt.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Recipe not found");
                    return;
                }
                req.setAttribute("recipe", recipeOpt.get());
                req.setAttribute("mode", "edit");
                req.getRequestDispatcher("/WEB-INF/view/recipe-form.jsp").forward(req, resp);
            }
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();
        RecipeService service = recipeService();

        if ("/recipe/delete".equals(path)) {
            Long id = parseLongOrNull(req.getParameter("id"));
            if (id != null) {
                service.deleteRecipe(id);
            }
            resp.sendRedirect(req.getContextPath() + "/recipes");
            return;
        }

        if ("/recipe/save".equals(path)) {
            Long id = parseLongOrNull(req.getParameter("id"));
            String title = req.getParameter("title");
            String instruction = req.getParameter("instruction");

            try {
                List<RecipeItem> items = parseItems(req);
                Recipe draft = Recipe.builder()
                        .id(id)
                        .title(title)
                        .instruction(instruction)
                        .items(items)
                        .build();

                Recipe saved = (id == null)
                        ? service.createRecipe(draft)
                        : service.updateRecipe(id, draft);
                resp.sendRedirect(req.getContextPath() + "/recipe?id=" + saved.getId());
            } catch (IllegalArgumentException ex) {
                Recipe fallbackDraft = Recipe.builder()
                        .id(id)
                        .title(title)
                        .instruction(instruction)
                        .items(parseItemsLenient(req))
                        .build();
                req.setAttribute("error", ex.getMessage());
                req.setAttribute("recipe", fallbackDraft);
                req.setAttribute("mode", id == null ? "create" : "edit");
                req.getRequestDispatcher("/WEB-INF/view/recipe-form.jsp").forward(req, resp);
            }
        }
    }

    private List<RecipeItem> parseItems(HttpServletRequest req) {
        String[] names = req.getParameterValues("ingredientName");
        String[] amounts = req.getParameterValues("amount");
        String[] units = req.getParameterValues("unit");

        if (names == null) {
            return List.of();
        }

        List<RecipeItem> result = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            String name = names[i] != null ? names[i].trim() : "";
            String amountStr = (amounts != null && i < amounts.length && amounts[i] != null)
                    ? amounts[i].trim().replace(',', '.')
                    : "";
            String unit = (units != null && i < units.length && units[i] != null)
                    ? units[i].trim()
                    : "";

            if (name.isEmpty() && amountStr.isEmpty() && unit.isEmpty()) {
                continue;
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Вкажіть назву для кожного заповненого інгредієнта.");
            }
            BigDecimal amount;
            if (amountStr.isEmpty()) {
                amount = BigDecimal.ZERO;
            } else {
                try {
                    amount = new BigDecimal(amountStr);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(
                            "Некоректна кількість для «" + name + "»: «" + amounts[i].trim() +
                                    "». Введіть одне число (наприклад, 450 або 1.5)."
                    );
                }
            }
            result.add(new RecipeItem(name, amount, unit));
        }
        return result;
    }

    private List<RecipeItem> parseItemsLenient(HttpServletRequest req) {
        String[] names = req.getParameterValues("ingredientName");
        String[] amounts = req.getParameterValues("amount");
        String[] units = req.getParameterValues("unit");
        if (names == null) {
            return List.of();
        }
        List<RecipeItem> result = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            String name = names[i] != null ? names[i].trim() : "";
            String amountStr = (amounts != null && i < amounts.length && amounts[i] != null)
                    ? amounts[i].trim().replace(',', '.')
                    : "";
            String unit = (units != null && i < units.length && units[i] != null)
                    ? units[i].trim()
                    : "";
            if (name.isEmpty() && amountStr.isEmpty() && unit.isEmpty()) {
                continue;
            }
            BigDecimal amount = BigDecimal.ZERO;
            try {
                if (!amountStr.isEmpty()) {
                    amount = new BigDecimal(amountStr);
                }
            } catch (NumberFormatException ignored) {
            }
            result.add(new RecipeItem(name, amount, unit));
        }
        return result;
    }

    private Long parseLongOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
