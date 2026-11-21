package com.anya.recipemanager.model.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class IngredientNameNormalizerTest {
    private final IngredientNameNormalizer normalizer = new IngredientNameNormalizer();

    @Test
    void shouldNormalizeCaseAndTrimSpaces() {
        assertEquals("курка", normalizer.normalize("  КУРКА  "));
        assertEquals("курка", normalizer.normalize("Курка"));
    }

    @Test
    void shouldCollapseMultipleInnerSpaces() {
        assertEquals("куряче філе", normalizer.normalize("  Куряче    філе  "));
    }

    @Test
    void shouldReturnEmptyStringForNullOrBlank() {
        assertEquals("", normalizer.normalize(null));
        assertEquals("", normalizer.normalize(""));
        assertEquals("", normalizer.normalize("    "));
    }
}
