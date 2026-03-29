--liquibase formatted sql

--changeset author:001-insert-test-data
INSERT INTO users (id, name, email, password, role, created_at, updated_at)
VALUES (gen_random_uuid(),
        'Test admin',
        'admin@example.com',
        '$2a$10$tV8MJDhPfCRQJdCXp5JKZe6Hidd016ogC7stE.TfDA7Ml5O5jFvx6', -- "admin"
        'ADMIN',
        NOW(),
        NOW()) ON CONFLICT (email) DO NOTHING;

INSERT INTO users (id, name, email, password, role, created_at, updated_at)
SELECT gen_random_uuid(),
       'User ' || i,
       'user' || i || '@example.com',
       '$2a$10$FOrF1DQ4bIK.0SRAj8.5M.CRHhut2uPhAB/hUGL/Ll5xLINQVNjoC', -- "password"
       'USER',
       NOW() - (random() * interval '90 days'),
       NOW() - (random() * interval '30 days')
FROM generate_series(1, 500) AS i ON CONFLICT (email) DO NOTHING;
