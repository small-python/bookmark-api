-- Creates the categories table for organizing bookmarks into groups
CREATE TABLE categories (
                            id          BIGSERIAL PRIMARY KEY,

    -- Category name — must be unique per user so two users can have same category names
                            name        VARCHAR(100) NOT NULL,

    -- Optional description of what the category is for
                            description VARCHAR(255),

    -- Links this category to the user who created it
                            user_id     BIGINT NOT NULL,

    -- Ensures category names are unique per user, not globally
                            CONSTRAINT uq_category_name_per_user UNIQUE (user_id, name),

    -- Deleting a user removes all their categories automatically
                            CONSTRAINT fk_category_user FOREIGN KEY (user_id)
                                REFERENCES users (id) ON DELETE CASCADE,

                            created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
                            updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);