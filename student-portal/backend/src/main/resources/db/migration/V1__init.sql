CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE grades (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES users(id),
    module_code VARCHAR(50) NOT NULL,
    module_title VARCHAR(255) NOT NULL,
    session VARCHAR(50) NOT NULL,
    grade NUMERIC(5,2) NOT NULL,
    published_at TIMESTAMP
);

CREATE TABLE timetables (
    id SERIAL PRIMARY KEY,
    program VARCHAR(255) NOT NULL,
    semester VARCHAR(50) NOT NULL,
    week_start DATE NOT NULL,
    data_json JSONB
);

CREATE TYPE request_type AS ENUM ('CERTIFICAT_SCOLARITE','ATTESTATION');
CREATE TYPE request_status AS ENUM ('DRAFT','SUBMITTED','IN_REVIEW','APPROVED','REJECTED','READY','DELIVERED');

CREATE TABLE requests (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES users(id),
    type request_type NOT NULL,
    status request_status NOT NULL,
    payload_json JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE request_files (
    id SERIAL PRIMARY KEY,
    request_id INTEGER NOT NULL REFERENCES requests(id) ON DELETE CASCADE,
    filename VARCHAR(255) NOT NULL,
    mime VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE threads (
    id SERIAL PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    created_by INTEGER NOT NULL REFERENCES users(id),
    last_message_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    thread_id INTEGER NOT NULL REFERENCES threads(id) ON DELETE CASCADE,
    sender_id INTEGER NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TYPE payment_status AS ENUM ('PENDING','SUCCEEDED','FAILED','REFUNDED');

CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES users(id),
    request_id INTEGER REFERENCES requests(id),
    amount_cents BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status payment_status NOT NULL,
    provider_ref VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE faqs (
    id SERIAL PRIMARY KEY,
    question VARCHAR(255) NOT NULL,
    answer TEXT NOT NULL
);

CREATE TABLE faq_tags (
    faq_id INTEGER REFERENCES faqs(id) ON DELETE CASCADE,
    tag VARCHAR(50)
);

-- Seed users
INSERT INTO users (email, password_hash, full_name, role) VALUES
 ('student1@school.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5Bsi9DD35J5IzyXHfVEWilR57Yj8HGa', 'Alice Martin', 'STUDENT'),
 ('student2@school.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5Bsi9DD35J5IzyXHfVEWilR57Yj8HGa', 'Bob Dupont', 'STUDENT'),
 ('staff@school.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5Bsi9DD35J5IzyXHfVEWilR57Yj8HGa', 'Claire Support', 'STAFF'),
 ('admin@school.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5Bsi9DD35J5IzyXHfVEWilR57Yj8HGa', 'David Admin', 'ADMIN');

-- Seed grades
INSERT INTO grades (student_id, module_code, module_title, session, grade, published_at) VALUES
 (1, 'MATH101', 'Analyse 1', '2024-S1', 14.5, NOW() - INTERVAL '10 days'),
 (1, 'PHY101', 'Physique', '2024-S1', 12.0, NOW() - INTERVAL '8 days'),
 (1, 'CS102', 'Programmation', '2024-S1', 16.5, NOW() - INTERVAL '5 days'),
 (2, 'CS102', 'Programmation', '2024-S1', 13.0, NOW() - INTERVAL '5 days');

-- Seed timetable
INSERT INTO timetables (program, semester, week_start, data_json) VALUES
 ('Informatique', 'S2', CURRENT_DATE, '{"events":[{"title":"Algèbre","start":"09:00","end":"11:00","day":"Monday"},{"title":"Projet","start":"14:00","end":"17:00","day":"Wednesday"},{"title":"Examen de mi-parcours","start":"10:00","end":"12:00","day":"Friday"}]}');

-- Seed requests
INSERT INTO requests (student_id, type, status, payload_json) VALUES
 (1, 'CERTIFICAT_SCOLARITE', 'READY', '{"reason":"Stage"}'),
 (1, 'ATTESTATION', 'IN_REVIEW', '{"details":"Bourse"}');

INSERT INTO request_files (request_id, filename, mime, url) VALUES
 (1, 'piece_identite.pdf', 'application/pdf', '/uploads/piece_identite.pdf');

-- Seed messaging
INSERT INTO threads (subject, created_by) VALUES
 ('Demande de certificat', 1);

INSERT INTO messages (thread_id, sender_id, content) VALUES
 (1, 1, 'Bonjour, je souhaite obtenir un certificat de scolarité.'),
 (1, 3, 'Bonjour Alice, nous traitons votre demande.');

-- Seed payments
INSERT INTO payments (student_id, request_id, amount_cents, currency, status, provider_ref) VALUES
 (1, 1, 2500, 'EUR', 'SUCCEEDED', 'sim-12345'),
 (1, NULL, 5000, 'EUR', 'FAILED', 'sim-67890');

-- Seed FAQs
INSERT INTO faqs (question, answer) VALUES
 ('Comment obtenir un certificat de scolarité ?', 'Créez une demande dans le menu E-guichet et choisissez certificat de scolarité.'),
 ('Comment payer mes frais ?', 'Utilisez la section Paiements pour créer une intention puis confirmez via le simulateur.'),
 ('Comment contacter le support ?', 'Utilisez la messagerie intégrée pour envoyer un message au guichet.');

INSERT INTO faq_tags (faq_id, tag) VALUES
 (1, 'certificat'),
 (1, 'administratif'),
 (2, 'paiement'),
 (3, 'support');
