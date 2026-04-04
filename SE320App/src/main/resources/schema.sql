CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    onboarding_complete BOOLEAN,
    onboarding_path VARCHAR(64),
    severity_level VARCHAR(64),
    streak_days INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS session_modules (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cbt_sessions (
    id UUID PRIMARY KEY,
    session_id BIGINT UNIQUE,
    user_id VARCHAR(255),
    session_type VARCHAR(255),
    status VARCHAR(64),
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    module_id UUID,
    title VARCHAR(255),
    description TEXT,
    duration_minutes INTEGER,
    order_index INTEGER,
    CONSTRAINT fk_cbt_sessions_module
        FOREIGN KEY (module_id) REFERENCES session_modules(id)
);

CREATE TABLE IF NOT EXISTS cbt_session_objectives (
    session_id UUID NOT NULL,
    objective VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_cbt_objectives_session
        FOREIGN KEY (session_id) REFERENCES cbt_sessions(id)
);

CREATE TABLE IF NOT EXISTS cbt_session_modalities (
    session_id UUID NOT NULL,
    modality VARCHAR(64) NOT NULL,
    CONSTRAINT fk_cbt_modalities_session
        FOREIGN KEY (session_id) REFERENCES cbt_sessions(id)
);

CREATE TABLE IF NOT EXISTS cognitive_distortions (
    id VARCHAR(128) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS cognitive_distortion_examples (
    distortion_id VARCHAR(128) NOT NULL,
    example VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_distortion_examples_distortion
        FOREIGN KEY (distortion_id) REFERENCES cognitive_distortions(id)
);

CREATE TABLE IF NOT EXISTS diary_entries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    situation TEXT NOT NULL,
    automatic_thought TEXT NOT NULL,
    alternative_thought TEXT NOT NULL,
    mood_before INTEGER NOT NULL,
    mood_after INTEGER NOT NULL,
    belief_rating_before INTEGER,
    belief_rating_after INTEGER,
    created_at TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_diary_entries_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS diary_entry_emotions (
    diary_entry_id UUID NOT NULL,
    emotion VARCHAR(255) NOT NULL,
    rating INTEGER,
    CONSTRAINT fk_diary_emotions_entry
        FOREIGN KEY (diary_entry_id) REFERENCES diary_entries(id)
);

CREATE TABLE IF NOT EXISTS diary_entry_distortions (
    diary_entry_id UUID NOT NULL,
    distortion_id VARCHAR(128) NOT NULL,
    PRIMARY KEY (diary_entry_id, distortion_id),
    CONSTRAINT fk_diary_distortions_entry
        FOREIGN KEY (diary_entry_id) REFERENCES diary_entries(id),
    CONSTRAINT fk_diary_distortions_distortion
        FOREIGN KEY (distortion_id) REFERENCES cognitive_distortions(id)
);

CREATE TABLE IF NOT EXISTS user_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    cbt_session_id UUID NOT NULL,
    status VARCHAR(64) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    mood_before INTEGER,
    mood_after INTEGER,
    CONSTRAINT fk_user_sessions_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_sessions_cbt_session
        FOREIGN KEY (cbt_session_id) REFERENCES cbt_sessions(id),
    CONSTRAINT chk_user_sessions_mood_before
        CHECK (mood_before IS NULL OR (mood_before BETWEEN 1 AND 10)),
    CONSTRAINT chk_user_sessions_mood_after
        CHECK (mood_after IS NULL OR (mood_after BETWEEN 1 AND 10))
);

CREATE TABLE IF NOT EXISTS chat_messages (
    id UUID PRIMARY KEY,
    user_session_id UUID NOT NULL,
    role VARCHAR(32) NOT NULL,
    content TEXT NOT NULL,
    modality VARCHAR(32) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_chat_messages_user_session
        FOREIGN KEY (user_session_id) REFERENCES user_sessions(id)
);

CREATE TABLE IF NOT EXISTS trusted_contacts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(64) NOT NULL,
    relationship VARCHAR(128) NOT NULL,
    CONSTRAINT fk_trusted_contacts_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS coping_strategies (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(128),
    description TEXT NOT NULL
);
