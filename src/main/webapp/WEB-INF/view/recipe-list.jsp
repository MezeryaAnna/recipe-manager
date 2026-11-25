<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Усі рецепти — Recipe Manager</title>
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
        <h1>Усі рецепти</h1>
        <p class="subtitle">Натисніть на назву рецепту, щоб переглянути інгредієнти та спосіб приготування.</p>

        <c:choose>
            <c:when test="${empty recipes}">
                <div class="recipe-card">
                    <p class="meta">Рецептів ще немає. Додайте перший рецепт, щоб розпочати.</p>
                    <div class="actions-bar">
                        <a class="btn" href="<c:url value='/recipe/new'/>">+ Додати рецепт</a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="recipe-list-stack">
                    <c:forEach var="r" items="${recipes}">
                        <div class="recipe-row-card">
                            <div>
                                <a class="recipe-card-title" href="<c:url value='/recipe'><c:param name='id' value='${r.id}'/></c:url>">
                                    <c:out value="${r.title}"/>
                                </a>
                                <span class="meta">Інгредієнтів: <c:out value="${r.items.size()}"/></span>
                            </div>
                            <a class="btn btn-secondary" href="<c:url value='/recipe'><c:param name='id' value='${r.id}'/></c:url>">
                                Відкрити
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</div>
</body>
</html>