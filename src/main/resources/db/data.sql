-- password 1111
INSERT INTO user VALUES (1, 'user1', 'user1@gmail.com', '{bcrypt}$2a$10$lPZZzaFoD2XFCxdfW.GmuOcdJcBdqoUOkqZrmJDYjEcyHiKQd4z5K', '010-111-1111');
-- password 2222
INSERT INTO user VALUES (2, 'user2', 'user2@gmail.com', '{bcrypt}$2a$10$N0XMEG4Qf2XES1FZMDR1wezPVAzxB6bwPle.R3OL.nmuiOBm9tEKS', '010-222-2222');
-- password 3333
INSERT INTO user VALUES (3, 'admin', 'admin@gmail.com', '{bcrypt}$2a$10$hCfnBuKkt.3NAf4qtd66cOwyQJ/6N8lrITVmtwNrOVloxLwZXf4uu', '010-333-3333');

INSERT INTO role VALUES (1, 'ROLE_TEMPORARY_USER');
INSERT INTO role VALUES (2, 'ROLE_USER');
INSERT INTO role VALUES (3, 'ROLE_ADMIN');

INSERT INTO privilege VALUES (1, 'COMMUNICATION_AUTHORITY');
INSERT INTO privilege VALUES (2, 'WORK_AUTHORITY');
INSERT INTO privilege VALUES (3, 'TASK_AUTHORITY');
INSERT INTO privilege VALUES (4, 'CONFIG_AUTHORITY');

INSERT INTO user_role VALUES (1, 1);
INSERT INTO user_role VALUES (2, 1);
INSERT INTO user_role VALUES (2, 2);
INSERT INTO user_role VALUES (3, 1);
INSERT INTO user_role VALUES (3, 2);
INSERT INTO user_role VALUES (3, 3);

INSERT INTO role_privilege VALUES (1, 1);
INSERT INTO role_privilege VALUES (2, 2);
INSERT INTO role_privilege VALUES (2, 3);
INSERT INTO role_privilege VALUES (3, 4);