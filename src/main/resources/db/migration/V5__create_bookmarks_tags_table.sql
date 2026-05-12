-- Creates the join table that links bookmarks to tags (many-to-many relationship)
-- A bookmark can have multiple tags, and a tag can belong to multiple bookmarks
CREATE TABLE bookmark_tags (

    -- References the bookmark being tagged
                               bookmark_id BIGINT NOT NULL,

    -- References the tag being applied
                               tag_id      BIGINT NOT NULL,

    -- Composite primary key — prevents the same tag being added to a bookmark twice
                               CONSTRAINT pk_bookmark_tags PRIMARY KEY (bookmark_id, tag_id),

    -- Deleting a bookmark removes all its tag associations automatically
                               CONSTRAINT fk_bookmark_tags_bookmark FOREIGN KEY (bookmark_id)
                                   REFERENCES bookmarks (id) ON DELETE CASCADE,

    -- Deleting a tag removes all its bookmark associations automatically
                               CONSTRAINT fk_bookmark_tags_tag FOREIGN KEY (tag_id)
                                   REFERENCES tags (id) ON DELETE CASCADE
);