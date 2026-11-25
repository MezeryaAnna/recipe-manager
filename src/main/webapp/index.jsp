<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Recipe Manager</title>
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
        <h1>Керування рецептами</h1>
        <p class="subtitle">Оберіть потрібний розділ для роботи з базою рецептів та підбору страв.</p>

        <div class="card-grid">
            <div class="recipe-card">
                <a class="recipe-card-title" href="<c:url value='/recipes'/>">Усі рецепти</a>
                <p class="meta">Перегляд каталогу страв, відкриття картки, редагування та видалення.</p>
            </div>
            <div class="recipe-card">
                <a class="recipe-card-title" href="<c:url value='/recipe/new'/>">+ Додати рецепт</a>
                <p class="meta">Створення нового рецепту з інгредієнтами, кількістю та інструкцією.</p>
            </div>
            <div class="recipe-card">
                <a class="recipe-card-title" href="<c:url value='/search'/>">Пошук за інгредієнтами</a>
                <p class="meta">Швидкий пошук страв за одним або кількома продуктами через кому.</p>
            </div>
            <div class="recipe-card">
                <a class="recipe-card-title" href="<c:url value='/fridge-match'/>">Що приготувати</a>
                <p class="meta">Підбір рецептів під наявні продукти з вибором стратегії ранжування.</p>
            </div>
        </div>
    </main>
</div>
</body>
</html>