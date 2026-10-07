package np.edu.cms.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "consultancy_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private static final String SERVER_URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/";

    private static final String DATABASE_URL =
            SERVER_URL + DB_NAME
                    + "?useSSL=false&serverTimezone=Asia/Kathmandu";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DATABASE_URL,
                DB_USER,
                DB_PASSWORD
        );
    }

    public static void initializeDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                SERVER_URL,
                DB_USER,
                DB_PASSWORD
        ); Statement statement = connection.createStatement()) {

            statement.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS " + DB_NAME
            );
        }

        createTables();
        insertDefaultUsers();
    }

    private static void createTables() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL,
                    full_name VARCHAR(100) NOT NULL,
                    role VARCHAR(20) NOT NULL
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    full_name VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    phone VARCHAR(30),
                    address VARCHAR(200),
                    education VARCHAR(100),
                    assigned_staff VARCHAR(50)
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS universities (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(150) NOT NULL,
                    country VARCHAR(80),
                    city VARCHAR(80),
                    website VARCHAR(200),
                    status VARCHAR(30)
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS applications (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    student_id INT NOT NULL,
                    university_id INT NOT NULL,
                    course VARCHAR(150),
                    intake VARCHAR(50),
                    status VARCHAR(40),
                    applied_date DATE,
                    FOREIGN KEY (student_id) REFERENCES students(id)
                        ON DELETE CASCADE,
                    FOREIGN KEY (university_id) REFERENCES universities(id)
                        ON DELETE CASCADE
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS appointments (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    student_id INT NOT NULL,
                    appointment_date DATE,
                    appointment_time VARCHAR(20),
                    purpose VARCHAR(200),
                    status VARCHAR(30),
                    FOREIGN KEY (student_id) REFERENCES students(id)
                        ON DELETE CASCADE
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS payments (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    student_id INT NOT NULL,
                    amount DECIMAL(12,2),
                    payment_date DATE,
                    method VARCHAR(30),
                    description VARCHAR(200),
                    FOREIGN KEY (student_id) REFERENCES students(id)
                        ON DELETE CASCADE
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS documents (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    student_id INT NOT NULL,
                    document_name VARCHAR(150),
                    document_type VARCHAR(50),
                    file_path VARCHAR(300),
                    uploaded_date DATE,
                    FOREIGN KEY (student_id) REFERENCES students(id)
                        ON DELETE CASCADE
                )
                """);
        }
    }

    private static void insertDefaultUsers() throws SQLException {
        String sql = """
            INSERT IGNORE INTO users
                (username, password, full_name, role)
            VALUES
                ('admin', 'admin123', 'System Administrator', 'ADMIN'),
                ('counselor', 'counselor123', 'Main Counselor', 'COUNSELOR')
            """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
}
