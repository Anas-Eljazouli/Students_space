ALTER TABLE requests
    ALTER COLUMN type TYPE VARCHAR(32) USING type::text,
    ALTER COLUMN status TYPE VARCHAR(32) USING status::text;

ALTER TABLE payments
    ALTER COLUMN status TYPE VARCHAR(32) USING status::text;
