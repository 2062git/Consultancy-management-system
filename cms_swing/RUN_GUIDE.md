# Run Guide - Consultancy Management System Swing Version

## 1. Install the required software

Install:
- JDK 17 or later
- IntelliJ IDEA
- MySQL Server

Maven does not need to be installed separately if IntelliJ uses its bundled Maven support.

## 2. Start MySQL

Open MySQL Workbench or MySQL Command Line Client and make sure the MySQL Server is running.

## 3. Check MySQL credentials

Open:

`src/main/java/np/edu/cms/config/DatabaseConnection.java`

Find:

`private static final String DB_USER = "root";`

`private static final String DB_PASSWORD = "root";`

Change the password to your actual MySQL root password.

## 4. Open the project

In IntelliJ:

File -> Open -> select the `consultancy-management-system-swing` folder.

IntelliJ should recognize `pom.xml` as a Maven project.

Wait until the Maven dependency download finishes.

## 5. Run the application

Open:

`src/main/java/np/edu/cms/Main.java`

Right-click `Main.java` and select:

Run 'Main.main()'

The application first creates the database and tables automatically.

## 6. Login

Admin:
- Username: admin
- Password: admin123

Counselor:
- Username: counselor
- Password: counselor123

## 7. Recommended presentation sequence

1. Login
2. Dashboard
3. Add a student
4. Add a university
5. Create an application for that student
6. Create an appointment
7. Record a payment
8. Add a document path
9. Show Staff management using admin
10. Logout and login as counselor

## 8. Date format

For dates, use:

YYYY-MM-DD

Example:

2026-10-04

## 9. If MySQL connection fails

The application will display the MySQL error.

Check:
- MySQL Server is running.
- Username is correct.
- Password is correct.
- MySQL is using port 3306.
- The MySQL user has permission to create the database.
