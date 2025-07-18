-- Create mail_queue table
CREATE TABLE mail_queue (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    to_address VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    sent_at TIMESTAMP WITHOUT TIME ZONE,
    attempts INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    error_message TEXT
);