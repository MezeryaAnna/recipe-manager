<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${mode eq 'edit' ? 'Редагування рецепту' : 'Новий рецепт'}"/> — Recipe Manager</title>
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
            <a href="<c:url value='/search'/>">Пошук за інгредієнтами</a>
            <a href="<c:url value='/fridge-match'/>">Що приготувати</a>
        </nav>
    </aside>

    <main class="main-panel">
        <h1><c:out value="${mode eq 'edit' ? 'Редагування рецепту' : 'Новий рецепт'}"/></h1>
        <p class="subtitle">Заповніть назву, опис кроків та будь-яку кількість інгредієнтів.</p>

        <c:if test="${not empty error}">
            <div class="error"><c:out value="${error}"/></div>
        </c:if>

        <form action="<c:url value='/recipe/save'/>" method="post">
            <c:if test="${not empty recipe.id}">
                <input type="hidden" name="id" value="${recipe.id}"/>
            </c:if>

            <div class="field">
                <label for="title">Назва</label>
                <input id="title" type="text" name="title"
                       placeholder="Наприклад: Сирники з родзинками"
                       value="<c:out value='${recipe.title}'/>" required/>
            </div>

            <div class="field">
                <label for="instruction">Інструкція</label>
                <textarea id="instruction" name="instruction" rows="6"
                          placeholder="Опишіть послідовність приготування..."><c:out value="${recipe.instruction}"/></textarea>
            </div>

            <h2>Інгредієнти</h2>
            <p class="meta" style="margin-top: -6px; margin-bottom: 12px;">
                Кількість вказуйте одним числом (наприклад: 450 або 1.5).
            </p>

            <datalist id="units-list">
                <option value="г">
                <option value="кг">
                <option value="мл">
                <option value="л">
                <option value="шт">
                <option value="ст. л.">
                <option value="ч. л.">
                <option value="скл.">
                <option value="дрібка">
                <option value="пучок">
                <option value="зубч.">
                <option value="за смаком">
            </datalist>

            <div id="ingredients-container">
                <c:forEach var="item" items="${recipe.items}">
                    <div class="ingredient-row" style="grid-template-columns: 2fr 1fr 1.1fr auto;">
                        <input type="text" name="ingredientName" placeholder="Інгредієнт"
                               value="<c:out value='${item.ingredientName}'/>"/>
                        <input type="number" step="any" min="0" name="amount" placeholder="Кількість"
                               value="<c:out value='${item.amount}'/>"/>
                        <input type="text" name="unit" list="units-list" placeholder="Од. вим. (г, ст. л., шт)"
                               value="<c:out value='${item.unit}'/>"/>
                        <button type="button" class="btn-secondary" onclick="removeIngredientRow(this)" title="Видалити рядок">✕</button>
                    </div>
                </c:forEach>

                <c:if test="${empty recipe.items}">
                    <c:forEach begin="1" end="4">
                        <div class="ingredient-row" style="grid-template-columns: 2fr 1fr 1.1fr auto;">
                            <input type="text" name="ingredientName" placeholder="Інгредієнт"/>
                            <input type="number" step="any" min="0" name="amount" placeholder="Кількість"/>
                            <input type="text" name="unit" list="units-list" placeholder="Од. вим. (г, ст. л., шт)"/>
                            <button type="button" class="btn-secondary" onclick="removeIngredientRow(this)" title="Видалити рядок">✕</button>
                        </div>
                    </c:forEach>
                </c:if>
            </div>

            <div style="margin-top: 8px; margin-bottom: 20px;">
                <button type="button" class="btn-secondary" onclick="addIngredientRow()">+ Додати ще інгредієнт</button>
            </div>

            <div class="actions-bar">
                <button type="submit">Зберегти</button>
                <a class="btn btn-secondary" href="<c:url value='/recipes'/>">Скасувати</a>
            </div>
        </form>
    </main>
</div>

<script>
    function addIngredientRow() {
        const container = document.getElementById('ingredients-container');
        const row = document.createElement('div');
        row.className = 'ingredient-row';
        row.style.gridTemplateColumns = '2fr 1fr 1.1fr auto';
        row.innerHTML = `
            <input type="text" name="ingredientName" placeholder="Інгредієнт"/>
            <input type="number" step="any" min="0" name="amount" placeholder="Кількість"/>
            <input type="text" name="unit" list="units-list" placeholder="Од. вим. (г, ст. л., шт)"/>
            <button type="button" class="btn-secondary" onclick="removeIngredientRow(this)" title="Видалити рядок">✕</button>
        `;
        container.appendChild(row);
    }

    function removeIngredientRow(btn) {
        const container = document.getElementById('ingredients-container');
        if (container.children.length > 1) {
            btn.parentElement.remove();
        } else {
            btn.parentElement.querySelectorAll('input').forEach(i => i.value = '');
        }
    }
</script>
</body>
</html>