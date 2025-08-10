CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    roles VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, roles),
    FOREIGN KEY (user_id) REFERENCES users(id)
);