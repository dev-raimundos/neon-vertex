CREATE TABLE home.role_permissions (
    role_id       UUID NOT NULL REFERENCES home.roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES home.permissions(id) ON DELETE CASCADE,

    PRIMARY KEY (role_id, permission_id)
);