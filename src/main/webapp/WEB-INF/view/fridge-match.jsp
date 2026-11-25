<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Що приготувати — Recipe Manager</title>
    <link rel="stylesheet" href="<c:url value='/static/style.css'/>">
</head>
<body>
<div class="app-shell">
    <aside class="sidebar">
        <div class="brand">
            <span class="brand-badge">RM</span>
            <span>Recipe Manager</span>
        </div>
        <nav class="side-nav">
            <a href="<c:url value='/'/>">Головна</a>
            <a href="<c:url value='/recipes'/>">Усі рецепти</a>
            <a href="<c:url value='/recipe/new'/>">+ Новий рецепт</a>
            <a href="<c:url value='/search'/>">Пошук за інгредієнтами</a>
            <a href="<c:url value='/fridge-match'/>">Що приготувати</a>
        </nav>
    </aside>

    <main class="main-panel">
        <h1>Підбір рецептів за наявними продуктами</h1>
        <p class="subtitle">Вкажіть продукти, які є вдома, та оберіть спосіб ранжування страв.</p>

        <form action="<c:url value='/fridge-match'/>" method="post">
            <div class="field">
                <label for="ingredients">Продукти в наявності (через кому)</label>
                <textarea id="ingredients" name="ingredients" rows="3"
                          placeholder="яйця, сир, помідор"><c:out value="${ingredientsInput}"/></textarea>
            </div>

            <div class="field">
                <label for="strategy">Стратегія ранжування</label>
                <select id="strategy" name="strategy">
                    <option value="percent" ${strategy eq 'percent' ? 'selected' : ''}>
                        За відсотком збігу (ByPercentStrategy)
                    </option>
                    <option value="fewest-missing" ${strategy eq 'fewest-missing' ? 'selected' : ''}>
                        За мінімумом відсутніх інгредієнтів (ByFewestMissingStrategy)
                    </option>
                </select>
            </div>

            <button type="submit">Підібрати рецепти</button>
        </form>

        <c:if test="${not empty matches}">
            <h2>Підходящі рецепти</h2>
            <div class="recipe-list-stack">
                <c:forEach var="m" items="${matches}">
                    <div class="recipe-row-card">
                        <div>
                            <a class="recipe-card-title" href="<c:url value='/recipe'><c:param name='id' value='${m.recipe().id}'/></c:url>">
                                <c:out value="${m.recipe().title}"/>
                            </a>
                            <div class="meta">
                                Бракує інгредієнтів: <c:out value="${m.matchResult().missingIngredients().size()}"/>
                                <c:if test="${not empty m.matchResult().missingIngredients()}">
                                    — <c:out value="${m.matchResult().missingIngredients()}"/>
                                </c:if>
                            </div>
                        </div>
                        <span class="pill pill-warm">
                            Збіг: <fmt:formatNumber value="${m.matchResult().matchPercentage()}" minFractionDigits="0" maxFractionDigits="1"/>%
                        </span>
                    </div>
                </c:forEach>
            </div>
        </c:if>
    </main>
</div>
</body>
</html>