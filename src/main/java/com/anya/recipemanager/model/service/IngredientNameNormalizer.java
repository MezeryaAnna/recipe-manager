package com.anya.recipemanager.model.service;
import java.util.Locale;


public class IngredientNameNormalizer {
        private final Locale DEFAULT_LOCALE = Locale.ROOT;
        private final String WHITESPACE_PATTERN = "\\s+";
        private final String SINGLE_SPACE = " ";
        private final String EMPTY = "";

        public String normalize(String rawName) {
            if (rawName == null) {
                return EMPTY;
            }
            String trimmed = rawName.trim();
            if (trimmed.isEmpty()) {
                return EMPTY;
            }
            return trimmed.replaceAll(WHITESPACE_PATTERN, SINGLE_SPACE).toLowerCase(DEFAULT_LOCALE);
        }
    }
