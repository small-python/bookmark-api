-- Creates the tags table for labeling bookmarks with flexible keywords
CREATE TABLE tags (
                      id          BIGSERIAL PRIMARY KEY,

    -- Tag name — must be unique per user so two users can have the same tag names
                      name        VARCHAR(50) NOT NULL,

    -- Links this tag to the user who created it
                      user_id     BIGINT NOT NULL,

    -- Ensures tag names are unique per user, not globally
                      CONSTRAINT uq_tag_name_per_user UNIQUE (user_id, name),

    -- Deleting a user removes all their tags automatically
                      CONSTRAINT fk_tag_user FOREIGN KEY (user_id)
                          REFERENCES users (id) ON DELETE CASCADE,

                      created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
                      updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);