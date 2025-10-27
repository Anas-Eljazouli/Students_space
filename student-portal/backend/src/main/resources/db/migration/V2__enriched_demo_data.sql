-- Enriched demo data for a fuller UI experience

-- Additional grades for demo students
INSERT INTO grades (id, student_id, module_code, module_title, session, grade, published_at) VALUES
    (5, 1, 'ENG201', 'Communication professionnelle', '2024-S1', 15.2, NOW() - INTERVAL '4 days'),
    (6, 1, 'HIS110', 'Culture europeenne', '2024-S1', 11.8, NOW() - INTERVAL '2 days'),
    (7, 2, 'MATH101', 'Analyse 1', '2024-S1', 13.7, NOW() - INTERVAL '9 days'),
    (8, 2, 'PHY101', 'Physique', '2024-S1', 14.1, NOW() - INTERVAL '7 days'),
    (9, 2, 'ENG201', 'Communication professionnelle', '2024-S1', 16.0, NOW() - INTERVAL '1 day');

-- Extra timetable weeks with richer schedules
INSERT INTO timetables (id, program, semester, week_start, data_json) VALUES
    (2, 'Informatique', 'S2', CURRENT_DATE + INTERVAL '7 days', '{"events":[{"title":"Projet tutore","start":"13:00","end":"16:00","day":"Monday"},{"title":"Atelier DevOps","start":"09:30","end":"11:30","day":"Tuesday"},{"title":"Sport","start":"15:00","end":"17:00","day":"Thursday"}]}'),
    (3, 'Informatique', 'S2', CURRENT_DATE + INTERVAL '14 days', '{"events":[{"title":"Conference invite","start":"10:00","end":"12:00","day":"Wednesday"},{"title":"TP Reseaux","start":"08:30","end":"11:30","day":"Friday"}]}');

-- Richer administrative request history
INSERT INTO requests (id, student_id, type, status, payload_json, created_at, updated_at) VALUES
    (3, 1, 'CERTIFICAT_SCOLARITE', 'DELIVERED', '{"reason":"Visa Canada"}', NOW() - INTERVAL '20 days', NOW() - INTERVAL '3 days'),
    (4, 1, 'ATTESTATION', 'APPROVED', '{"details":"Logement CROUS"}', NOW() - INTERVAL '12 days', NOW() - INTERVAL '1 day'),
    (5, 2, 'CERTIFICAT_SCOLARITE', 'READY', '{"reason":"Stage ete"}', NOW() - INTERVAL '9 days', NOW() - INTERVAL '2 days'),
    (6, 2, 'ATTESTATION', 'IN_REVIEW', '{"details":"Aide financiere"}', NOW() - INTERVAL '4 days', NOW() - INTERVAL '2 days'),
    (7, 2, 'ATTESTATION', 'DELIVERED', '{"details":"Transport"}', NOW() - INTERVAL '30 days', NOW() - INTERVAL '10 days');

INSERT INTO request_files (id, request_id, filename, mime, url, uploaded_at) VALUES
    (2, 3, 'justificatif_stage.pdf', 'application/pdf', '/uploads/justificatif_stage.pdf', NOW() - INTERVAL '18 days'),
    (3, 5, 'contrat_stage.pdf', 'application/pdf', '/uploads/contrat_stage.pdf', NOW() - INTERVAL '8 days'),
    (4, 6, 'avis_imposition.jpg', 'image/jpeg', '/uploads/avis_imposition.jpg', NOW() - INTERVAL '3 days');

-- Messaging threads with ongoing discussions
INSERT INTO threads (id, subject, created_by, last_message_at) VALUES
    (2, 'Suivi paiement frais', 1, NOW() - INTERVAL '1 day'),
    (3, 'Attestation pour transport', 2, NOW() - INTERVAL '2 days');

INSERT INTO messages (id, thread_id, sender_id, content, created_at) VALUES
    (3, 2, 1, 'Bonjour, je n''arrive pas a finaliser mon paiement.', NOW() - INTERVAL '2 days'),
    (4, 2, 3, 'Bonjour Alice, nous avons relance le simulateur.', NOW() - INTERVAL '1 day'),
    (5, 2, 1, 'Merci, le paiement est passe !', NOW() - INTERVAL '20 hours'),
    (6, 3, 2, 'Bonjour, j''ai besoin d''une attestation pour mon abonnement.', NOW() - INTERVAL '3 days'),
    (7, 3, 3, 'Bonjour Bob, la demande est en cours de validation.', NOW() - INTERVAL '2 days');

-- Additional payments linked to requests with varied outcomes
INSERT INTO payments (id, student_id, request_id, amount_cents, currency, status, provider_ref, created_at, updated_at) VALUES
    (3, 1, 3, 1500, 'EUR', 'SUCCEEDED', 'sim-11223', NOW() - INTERVAL '18 days', NOW() - INTERVAL '3 days');

INSERT INTO payments (id, student_id, request_id, amount_cents, currency, status, provider_ref, created_at, updated_at) VALUES
    (4, 1, NULL, 8900, 'EUR', 'PENDING', 'sim-44556', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
    (5, 2, 5, 2500, 'EUR', 'SUCCEEDED', 'sim-77889', NOW() - INTERVAL '7 days', NOW() - INTERVAL '2 days'),
    (6, 2, 6, 2500, 'EUR', 'FAILED', 'sim-99001', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days');

-- Refresh sequences after explicit id inserts
SELECT setval('grades_id_seq', (SELECT MAX(id) FROM grades));
SELECT setval('timetables_id_seq', (SELECT MAX(id) FROM timetables));
SELECT setval('requests_id_seq', (SELECT MAX(id) FROM requests));
SELECT setval('request_files_id_seq', (SELECT MAX(id) FROM request_files));
SELECT setval('threads_id_seq', (SELECT MAX(id) FROM threads));
SELECT setval('messages_id_seq', (SELECT MAX(id) FROM messages));
SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));
