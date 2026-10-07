CREATE DATABASE IF NOT EXISTS consultancy_db;
USE consultancy_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(30),
    address VARCHAR(200),
    education VARCHAR(100),
    assigned_staff VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS universities (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    country VARCHAR(80),
    city VARCHAR(80),
    website VARCHAR(200),
    status VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    university_id INT NOT NULL,
    course VARCHAR(150),
    intake VARCHAR(50),
    status VARCHAR(40),
    applied_date DATE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (university_id) REFERENCES universities(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    appointment_date DATE,
    appointment_time VARCHAR(20),
    purpose VARCHAR(200),
    status VARCHAR(30),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    amount DECIMAL(12,2),
    payment_date DATE,
    method VARCHAR(30),
    description VARCHAR(200),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS documents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    document_name VARCHAR(150),
    document_type VARCHAR(50),
    file_path VARCHAR(300),
    uploaded_date DATE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

INSERT IGNORE INTO users
    (username, password, full_name, role)
VALUES
    ('admin', 'admin123', 'System Administrator', 'ADMIN'),
    ('counselor', 'counselor123', 'Main Counselor', 'COUNSELOR');
