CREATE TABLE IF NOT EXISTS recipe (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    instruction TEXT
);

CREATE TABLE IF NOT EXISTS ingredient (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS recipe_ingredient (
    recipe_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    unit VARCHAR(64) NOT NULL,
    CONSTRAINT pk_recipe_ingredient PRIMARY KEY (recipe_id, ingredient_id),
    CONSTRAINT fk_ri_recipe
        FOREIGN KEY (recipe_id) REFERENCES recipe (id) ON DELETE CASCADE,
    CONSTRAINT fk_ri_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredient (id)
);

CREATE INDEX IF NOT EXISTS idx_ingredient_lower_name
    ON ingredient (LOWER(name));