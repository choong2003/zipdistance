-- Insert roles
INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_USER');

-- Insert sample users pass#hashed_password1,hashed_password2
INSERT INTO users (id, name, email, password_hash) VALUES
('1e8f39f1-1234-4a56-9876-abcdef123456', 'admin_user', 'admin@example.com', '$2a$10$Y9HEaw2qi1xyJN4QH6Cha.B314F.9B9kfRuqvI.OpIhmEstqqL3mq'),
('2a5f41a2-2345-4b67-8765-bcdef2345678', 'normal_user', 'normal_user@example.com', '$2a$10$pmWh7az4O4ftqLeURFfb7O6tdYH14gAUpe8M4wiR0.ex5JHvcGSSu');

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id from users where name = 'admin_user'), 1),  -- admin_user -> ADMIN
((SELECT id from users where name = 'normal_user'), 2);  -- normal_user -> NORMAL USER

-- Create some permissions
INSERT INTO permissions (name) VALUES ('CREATE_USER'),('UPDATE_USER'), ('CREATE_POSTAL'), ('UPDATE_POSTAL'), ('VIEW_POSTAL'),('CALCULATE_DISTANCE');

-- Admin has all permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6);

-- Normal User has view POSTAL and calculate only
INSERT INTO role_permissions (role_id, permission_id) VALUES
(2, 5), (2, 6);
