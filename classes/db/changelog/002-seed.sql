--liquibase formatted sql
--changeset dev:002-seed

INSERT INTO users (id, email, password, full_name, active, created_at)
VALUES (
    gen_random_uuid(),
    'admin@proctoring.kz',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'System Administrator',
    true,
    NOW()
) ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, roles)
SELECT id, 'ROLE_ADMIN' FROM users WHERE email = 'admin@proctoring.kz'
ON CONFLICT DO NOTHING;
