-- Believe 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS believe DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE believe;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(256) NOT NULL,
    nickname VARCHAR(64),
    email VARCHAR(128),
    phone VARCHAR(32),
    avatar VARCHAR(256),
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted INT DEFAULT 0,
    UNIQUE KEY uk_username (username)
);

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    code VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted INT DEFAULT 0,
    UNIQUE KEY uk_code (code)
);

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    code VARCHAR(128),
    parent_id BIGINT DEFAULT 0,
    type INT DEFAULT 1 COMMENT '1=菜单 2=按钮 3=API',
    path VARCHAR(256),
    icon VARCHAR(64),
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted INT DEFAULT 0,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted INT DEFAULT 0,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
);

-- 初始化管理员角色
INSERT IGNORE INTO sys_role (name, code, description, sort) VALUES ('管理员', 'ROLE_ADMIN', '系统管理员', 1);
INSERT IGNORE INTO sys_role (name, code, description, sort) VALUES ('普通用户', 'ROLE_USER', '普通用户', 2);

-- 初始化权限
INSERT IGNORE INTO sys_permission (name, code, type) VALUES ('用户管理', 'user:manage', 1);
INSERT IGNORE INTO sys_permission (name, code, type) VALUES ('角色管理', 'role:manage', 1);
INSERT IGNORE INTO sys_permission (name, code, type) VALUES ('权限管理', 'perm:manage', 1);

-- 将管理员角色绑定到所有权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p WHERE r.code = 'ROLE_ADMIN';
