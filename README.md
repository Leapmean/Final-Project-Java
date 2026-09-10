# Kokoro Library System

A Java + SQLite desktop application based on the supplied Kokoro presentation.

## Features
- User/Admin login 
- Sign up with USER or ADMIN role
- Display all books in a table
- Admin: add, update and delete books
- SQLite database persistence
- Simple Swing user interface
- Prepared statements for database operations

## Project structure
- `Main.java` - starts the application and handles login/sign-up
- `Database.java` - creates the SQLite database and tables
- `User.java` - encapsulates user information
- `Book.java` - encapsulates book information
- `AuthService.java` - login and registration logic
- `LibraryFrame.java` - book management interface

## Run in IntelliJ
1. Open the project folder in IntelliJ IDEA.
2. Make sure JDK 17 or newer is selected.
3. Let Maven import dependencies.
4. Run `Main.java`.
5. Default administrator:
   - Username: `admin`
   - Password: `admin123`

The file `Kokoro.db` is created automatically after the first run.

## Run with Maven
```bash
mvn clean compile
mvn exec:java
```

## Database
Two tables are created automatically:
- `users(id, username, password, role)`
- `books(id, title, author, genre, registered_date)`

## Notes
The original presentation mentions Java Swing + JDate Picker, SQLite, DB Browser for SQLite, IntelliJ, and Git/GitHub. This completed implementation uses Swing and SQLite directly; the registered date is entered as text in ISO format (`YYYY-MM-DD`) to keep the project dependency-light.
