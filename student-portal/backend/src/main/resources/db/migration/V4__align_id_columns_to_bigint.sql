ALTER TABLE grades        DROP CONSTRAINT IF EXISTS grades_student_id_fkey;
ALTER TABLE requests      DROP CONSTRAINT IF EXISTS requests_student_id_fkey;
ALTER TABLE request_files DROP CONSTRAINT IF EXISTS request_files_request_id_fkey;
ALTER TABLE threads       DROP CONSTRAINT IF EXISTS threads_created_by_fkey;
ALTER TABLE messages      DROP CONSTRAINT IF EXISTS messages_thread_id_fkey;
ALTER TABLE messages      DROP CONSTRAINT IF EXISTS messages_sender_id_fkey;
ALTER TABLE payments      DROP CONSTRAINT IF EXISTS payments_student_id_fkey;
ALTER TABLE payments      DROP CONSTRAINT IF EXISTS payments_request_id_fkey;

ALTER TABLE users
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT;

ALTER TABLE grades
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT,
    ALTER COLUMN student_id TYPE BIGINT USING student_id::BIGINT;

ALTER TABLE timetables
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT;

ALTER TABLE requests
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT,
    ALTER COLUMN student_id TYPE BIGINT USING student_id::BIGINT;

ALTER TABLE request_files
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT,
    ALTER COLUMN request_id TYPE BIGINT USING request_id::BIGINT;

ALTER TABLE threads
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT,
    ALTER COLUMN created_by TYPE BIGINT USING created_by::BIGINT;

ALTER TABLE messages
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT,
    ALTER COLUMN thread_id TYPE BIGINT USING thread_id::BIGINT,
    ALTER COLUMN sender_id TYPE BIGINT USING sender_id::BIGINT;

ALTER TABLE payments
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT,
    ALTER COLUMN student_id TYPE BIGINT USING student_id::BIGINT,
    ALTER COLUMN request_id TYPE BIGINT USING request_id::BIGINT;

ALTER TABLE grades
    ADD CONSTRAINT grades_student_id_fkey FOREIGN KEY (student_id) REFERENCES users(id);

ALTER TABLE requests
    ADD CONSTRAINT requests_student_id_fkey FOREIGN KEY (student_id) REFERENCES users(id);

ALTER TABLE request_files
    ADD CONSTRAINT request_files_request_id_fkey FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE;

ALTER TABLE threads
    ADD CONSTRAINT threads_created_by_fkey FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE messages
    ADD CONSTRAINT messages_thread_id_fkey FOREIGN KEY (thread_id) REFERENCES threads(id) ON DELETE CASCADE;

ALTER TABLE messages
    ADD CONSTRAINT messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES users(id);

ALTER TABLE payments
    ADD CONSTRAINT payments_student_id_fkey FOREIGN KEY (student_id) REFERENCES users(id);

ALTER TABLE payments
    ADD CONSTRAINT payments_request_id_fkey FOREIGN KEY (request_id) REFERENCES requests(id);
