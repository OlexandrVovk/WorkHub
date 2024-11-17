CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE project_status AS ENUM ('ACTIVE', 'COMPLETED', 'ON_HOLD', 'CANCELLED');
CREATE TYPE task_status AS ENUM ('TODO', 'IN_PROGRESS', 'DONE', 'IN_REVIEW', 'CANCELLED');
CREATE TYPE task_priority AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'URGENT');
CREATE TYPE user_role AS ENUM ('OWNER', 'TEAM_MANAGER', 'MEMBER');

CREATE TABLE user_table (
    user_uuid UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    image_url TEXT
);

CREATE TABLE project_table (
    project_uuid UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    project_name VARCHAR(255) NOT NULL,
    project_status project_status NOT NULL DEFAULT 'ACTIVE',
    project_description TEXT
);

CREATE TABLE task_table (
    task_uuid UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    task_name VARCHAR(255) NOT NULL,
    task_description TEXT,
    task_status task_status NOT NULL DEFAULT 'TODO',
    priority task_priority NOT NULL DEFAULT 'MEDIUM',
    deadline TIMESTAMP,
    assignation_uuid UUID REFERENCES user_table(user_uuid) ON DELETE SET NULL,
    reporter_uuid UUID REFERENCES user_table(user_uuid) ON DELETE SET NULL,
    project_uuid UUID REFERENCES project_table(project_uuid) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_proj_connection (
    user_proj_con_uuid UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    project_uuid UUID REFERENCES project_table(project_uuid) ON DELETE CASCADE,
    user_uuid UUID REFERENCES user_table(user_uuid) ON DELETE CASCADE,
    user_role user_role NOT NULL DEFAULT 'MEMBER',
    UNIQUE(project_uuid, user_uuid)
);

CREATE INDEX idx_task_project ON task_table(project_uuid);
CREATE INDEX idx_task_assignee ON task_table(assignation_uuid);
CREATE INDEX idx_task_reporter ON task_table(reporter_uuid);
CREATE INDEX idx_user_proj_user ON user_proj_connection(user_uuid);
CREATE INDEX idx_user_proj_project ON user_proj_connection(project_uuid);