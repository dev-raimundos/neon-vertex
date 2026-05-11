CREATE TABLE home.user_roles (
    user_id UUID NOT NULL REFERENCES home.users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES home.roles(id) ON DELETE CASCADE,

    PRIMARY KEY (user_id, role_id)
);