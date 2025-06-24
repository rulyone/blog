ALTER TABLE blog_post ADD COLUMN IF NOT EXISTS slug VARCHAR(64);

UPDATE blog_post
SET slug = LEFT(
    regexp_replace(
        regexp_replace(
            lower(title),
            '[^a-z0-9]+', '-', 'g'
        ),
        '-+', '-', 'g'
    ), 64
)
WHERE slug IS NULL;