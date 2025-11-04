INSERT INTO users (id, name, email, password, role, active, created_at, updated_at) 
VALUES (
    gen_random_uuid(), 
    'Admin', 
    'admin@geo7.com', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 
    'ADMIN', 
    true,
    NOW(),
    NOW()
);
