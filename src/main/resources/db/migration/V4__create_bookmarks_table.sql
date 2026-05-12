-- Creates the bookmarks table — the core entity of the application
CREATE TABLE bookmarks (
                           id            BIGSERIAL PRIMARY KEY,

    -- The title the user gives to the bookmark
                           title         VARCHAR(200) NOT NULL,

    -- The actual URL being bookmarked
                           url           TEXT NOT NULL,

    -- Optional notes the user wants to attach to the bookmark
                           description   TEXT,

    -- Links this bookmark to the user who created it
                           user_id       BIGINT NOT NULL,

    -- Optional category — a bookmark can exist without a category
                           category_id   BIGINT,

    -- Soft delete flag — deleted bookmarks are hidden but not removed from DB
                           is_deleted    BOOLEAN NOT NULL DEFAULT FALSE,

                           created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
                           updated_at    TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Deleting a user removes all their bookmarks automatically
                           CONSTRAINT fk_bookmark_user FOREIGN KEY (user_id)
                               REFERENCES users (id) ON DELETE CASCADE,

    -- Setting category to null if the category is deleted, not deleting the bookmark
                           CONSTRAINT fk_bookmark_category FOREIGN KEY (category_id)
                               REFERENCES categories (id) ON DELETE SET NULL
);