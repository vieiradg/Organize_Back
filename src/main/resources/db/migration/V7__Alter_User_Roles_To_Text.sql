ALTER TABLE user_roles ALTER COLUMN role TYPE TEXT USING role::text;

DROP TYPE IF EXISTS role_type;

ALTER TABLE user_roles ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role);