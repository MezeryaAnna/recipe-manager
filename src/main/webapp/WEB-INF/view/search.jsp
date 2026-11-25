<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Пошук за інгредієнтами — Recipe Manager</title>
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
        <h1>Пошук за інгредієнтами</h1>
        <p class="subtitle">Введіть один або кілька інгредієнтів через кому.</p>

        <form action="<c:url value='/search'/>" method="get">
            <div class="field">
                <label for="ingredients">Інгредієнти (через кому)</label>
                <input id="ingredients" type="text" name="ingredients"
                       placeholder="наприклад: яйця, молоко"
                       value="<c:out value='${query}'/>"/>
            </div>
            <button type="submit">Знайти</button>
        </form>

        <c:if test="${not empty query}">
            <h2>Результати пошуку</h2>
            <c:choose>
                <c:when test="${empty results}">
                    <p class="meta">За запитом «<c:out value="${query}"/>» нічого не знайдено.</p>
                </c:when>
                <c:otherwise>
                    <div class="recipe-list-stack">
                        <c:forEach var="r" items="${results}">
                            <div class="recipe-row-card">
                                <a class="recipe-card-title" href="<c:url value='/recipe'><c:param name='id' value='${r.id}'/></c:url>">
                                    <c:out value="${r.title}"/>
                                </a>
                                <span class="pill">Знайдено</span>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>
    </main>
</div>
</body>
</html>