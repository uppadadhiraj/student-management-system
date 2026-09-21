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
2. In `Main.java`, set your MySQL password:
   ```java
   static final String DB_PASSWORD = "your_password";
   ```
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
