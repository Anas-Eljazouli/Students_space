ALTER TABLE payments
    ADD COLUMN label VARCHAR(255),
    ADD COLUMN payment_method VARCHAR(100),
    ADD COLUMN justification_url VARCHAR(512),
    ADD COLUMN justification_name VARCHAR(255),
    ADD COLUMN justification_mime VARCHAR(128),
    ADD COLUMN status_notes TEXT;

UPDATE payments
SET status = 'PROCESSING'
WHERE status = 'PENDING';

UPDATE payments
SET label = COALESCE(label, 'Paiement'),
    payment_method = COALESCE(payment_method, 'SIMULATED');

ALTER TABLE payments
    ALTER COLUMN label SET NOT NULL,
    ALTER COLUMN payment_method SET NOT NULL,
    ALTER COLUMN status SET DEFAULT 'PROCESSING';

ALTER TABLE payments
    DROP COLUMN provider_ref;
