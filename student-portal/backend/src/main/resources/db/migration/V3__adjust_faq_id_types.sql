ALTER TABLE faq_tags
    DROP CONSTRAINT IF EXISTS faq_tags_faq_id_fkey;

ALTER TABLE faqs
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT;

ALTER TABLE faq_tags
    ALTER COLUMN faq_id TYPE BIGINT USING faq_id::BIGINT;

ALTER TABLE faq_tags
    ADD CONSTRAINT faq_tags_faq_id_fkey
        FOREIGN KEY (faq_id) REFERENCES faqs(id) ON DELETE CASCADE;
