CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    birth_year VARCHAR(10),
    password_hash VARCHAR(255) NOT NULL
);

SELECT * FROM users;