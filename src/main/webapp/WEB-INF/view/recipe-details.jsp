<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${recipe.title}"/> — Recipe Manager</title>
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
            <a href="<c:url value='/recipes'/>">&larr; До списку рецептів</a>
            <a href="<c:url value='/recipe/new'/>">+ Новий рецепт</a>
            <a href="<c:url value='/search'/>">Пошук за інгредієнтами</a>
            <a href="<c:url value='/fridge-match'/>">Що приготувати</a>
        </nav>
    </aside>

    <main class="main-panel">
        <h1><c:out value="${recipe.title}"/></h1>
        <p class="subtitle">Картка рецепту та перелік необхідних продуктів.</p>

        <h2>Інгредієнти</h2>
        <c:choose>
            <c:when test="${empty recipe.items}">
                <p class="meta">Інгредієнти не вказані.</p>
            </c:when>
            <c:otherwise>
                <div class="card-grid">
                    <c:forEach var="item" items="${recipe.items}">
                        <div class="ingredient-chip">
                            <strong><c:out value="${item.ingredientName}"/></strong>
                            <div class="meta">
                                <c:out value="${item.amount}"/> <c:out value="${item.unit}"/>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

        <h2>Інструкція приготування</h2>
        <div class="recipe-card">
            <p style="white-space: pre-line; margin: 0;"><c:out value="${recipe.instruction}"/></p>
        </div>

        <div class="actions-bar">
            <a class="btn btn-secondary" href="<c:url value='/recipe/edit'><c:param name='id' value='${recipe.id}'/></c:url>">
                Редагувати
            </a>
            <form action="<c:url value='/recipe/delete'/>" method="post"
                  onsubmit="return confirm('Видалити цей рецепт?');" style="margin: 0;">
                <input type="hidden" name="id" value="${recipe.id}"/>
                <button type="submit" class="danger">Видалити рецепт</button>
            </form>
        </div>
    </main>
</div>
</body>
</html>