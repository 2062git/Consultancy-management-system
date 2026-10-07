# Consultancy Management System - Java Swing + MySQL

A simple desktop Consultancy Management System developed using Java Swing, JDBC and MySQL.

## Main Modules
- Login
- Dashboard
- Student Management
- University Management
- Application Management
- Appointment Management
- Payment Management
- Document Management
- Staff Management

## Requirements
- JDK 17 or later
- IntelliJ IDEA, Eclipse or VS Code
- MySQL Server 8.0 or later
- Maven

## Database setup
1. Start MySQL Server.
2. Open `src/main/java/np/edu/cms/config/DatabaseConnection.java`.
3. Change `DB_USER` and `DB_PASSWORD` if your MySQL credentials are different.
4. Run the program. The application automatically creates the database and tables.

The default database name is `consultancy_db`.

## Login accounts
- Admin: username `admin`, password `admin123`
- Counselor: username `counselor`, password `counselor123`

## Run in IntelliJ IDEA
1. Open the project folder.
2. Allow IntelliJ to load the Maven project.
3. Wait for the MySQL Connector/J dependency to download.
4. Open `src/main/java/np/edu/cms/Main.java`.
5. Click the green Run button.
6. Login using one of the accounts above.

## Important
This is a student-friendly desktop application. Passwords are stored as plain text for simplicity. For a production system, passwords should be hashed and role permissions should be stronger.
