# Student Management System

A very small web app to add, view and delete students.
Built with plain Java (JDBC), MySQL, HTML and CSS. No frameworks.

## Files

- `Main.java` - web server, pages and database code
- `style.css` - styling
- `database.sql` - creates the database and table
- `lib/` - MySQL JDBC driver

## Setup

1. Create the database (run from this folder):
   ```
   mysql -u root -p < database.sql
   ```
2. Give the app your MySQL password through an environment variable (it is not stored in the code):
   ```
   # Windows PowerShell
   $env:DB_PASSWORD = "your-mysql-password"
   # Mac / Linux
   export DB_PASSWORD="your-mysql-password"
   ```
   Optional: `DB_USER` (default `root`) and `DB_URL` (default `jdbc:mysql://localhost:3306/student_db`).
3. Compile:
   ```
   javac -cp "lib/*" -d out Main.java
   ```
4. Run (on Mac/Linux use `:` instead of `;`):
   ```
   java -cp "out;lib/*" Main
   ```
5. Open http://localhost:8080

## Usage

- Fill in the form and click **Add Student**.
- Click **View Students** to see the table.
- Click **Delete** to remove a student.

Requires Java 11+ and MySQL.
