UPDATE users
SET role = 'PROFESSOR'
WHERE role = 'STAFF';

CREATE TABLE IF NOT EXISTS student_absences (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    module_code VARCHAR(50) NOT NULL,
    module_title VARCHAR(255) NOT NULL,
    session VARCHAR(50) NOT NULL,
    lesson_date DATE NOT NULL,
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_absence UNIQUE (student_id, module_code, lesson_date)
);
