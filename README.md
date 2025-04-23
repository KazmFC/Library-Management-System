# Library Management System

A Java-based desktop application for managing a library's authors, books, users, students, and book assignments. The system features a modern GUI with rounded buttons, dark/light theme support, and database integration using MySQL.

## Features
- **Manage Authors**: Add, edit, and delete authors (name, country).
- **Manage Books**: Add, edit, and delete books (name, count, papers, author).
- **Manage Users**: Add, edit, and delete users (name, email).
- **Manage Students**: Add, edit, and delete students (name, email).
- **Assign Author Books**: Assign books to authors.
- **Assign Student Books**: Assign books to students.
- **Theme Support**: Toggle between dark and light themes.
- **Modern UI**: Rounded buttons, consistent fonts, and responsive design.

## Prerequisites
- **Java Development Kit (JDK)**: Version 11 or higher.
- **MySQL Server**: Version 8.0 or higher.
- **MySQL Connector/J**: JDBC driver for MySQL (e.g., `mysql-connector-java-8.0.27.jar`).
- **IDE**: Eclipse, IntelliJ IDEA, or similar (optional for compilation).
- **Command Line**: For manual compilation and running (optional).

## Setup Instructions

### 1. Clone the Repository
Clone or download the project to your local machine.

### 2. Set Up the Database
1. **Install MySQL**: Ensure MySQL Server is running.
2. **Create Database**: Run the provided `library_management.sql` script to create the database and tables.
   ```bash
   mysql -u root -p < library_management.sql
   ```
   - Replace `root` with your MySQL username and enter your password when prompted.
   - The script creates the `library_management` database, tables, and inserts sample data.
3. **Verify Database**:
   - Log in to MySQL: `mysql -u root -p`
   - Check the database: `USE library_management; SHOW TABLES;`
   - Expected tables: `authors`, `books`, `users`, `students`, `author_books`, `student_books`.

### 3. Configure Database Connection
1. Open `src/com/library/db/DatabaseConnection.java`.
2. Update the database credentials (`URL`, `USER`, `PASSWORD`) to match your MySQL setup:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/library_management";
   private static final String USER = "root";
   private static final String PASSWORD = "your_password";
   ```
   - Replace `your_password` with your MySQL password.

### 4. Add MySQL Connector
1. Download the MySQL Connector/J JAR (e.g., `mysql-connector-java-8.0.27.jar`) from [MySQL's official site](https://dev.mysql.com/downloads/connector/j/).
2. Place the JAR in a directory (e.g., `lib/` in the project root).
3. Add the JAR to your project's classpath:
   - **Eclipse**: Right-click project → Properties → Java Build Path → Libraries → Add External JARs → Select the JAR.
   - **IntelliJ**: File → Project Structure → Libraries → Add → Select the JAR.
   - **Command Line**: Include the JAR in the `javac` and `java` commands (see below).

### 5. Compile and Run
#### Using an IDE
1. Import the project into your IDE.
2. Ensure `module-info.java` is configured:
   ```java
   module LibraryManagementSystem {
       requires java.desktop;
       requires java.sql;
   }
   ```
3. Build the project (e.g., Project → Clean in Eclipse, Build → Rebuild Project in IntelliJ).
4. Run `MainWindow.java` as a Java application.

#### Using Command Line
1. Compile the project:
   ```bash
   javac -cp "lib/mysql-connector-java-8.0.27.jar" -d bin src/com/library/*/*.java src/module-info.java
   ```
   - Replace `lib/mysql-connector-java-8.0.27.jar` with the path to your JAR.
2. Run the application:
   ```bash
   java -cp "bin;lib/mysql-connector-java-8.0.27.jar" com.library.ui.MainWindow
   ```
   - Use `:` instead of `;` on Unix-based systems (e.g., macOS, Linux).

## Usage
1. **Main Window**: Displays buttons to navigate to different management sections:
   - Manage Authors, Books, Users, Students, Assign Author Books, Assign Student Books, Exit.
2. **Theme Toggle**: Click the "Dark Mode" / "Light Mode" button in the top-right corner of any window to switch themes.
3. **Managing Records**:
   - **Add**: Fill in the fields and click the green "Add" button.
   - **Edit**: Click the "Edit" button in the table, update via dialogs, and confirm.
   - **Delete**: Click the "Delete" button in the table and confirm.
4. **Assigning Books**:
   - In Author/Student Books windows, select an author/student and book from dropdowns, then click "Assign".
   - Edit or delete assignments using the table buttons.
5. **Back**: Click the orange "Back" button to return to the Main Window.

## Project Structure
```
LibraryManagementSystem/
├── src/
│   ├── com/library/ui/
│   │   ├── MainWindow.java
│   │   ├── AuthorsWindow.java
│   │   ├── BooksWindow.java
│   │   ├── UsersWindow.java
│   │   ├── StudentsWindow.java
│   │   ├── AuthorBooksWindow.java
│   │   ├── StudentBooksWindow.java
│   │   ├── ThemeManager.java
│   │   ├── RoundedButton.java
│   │   ├── UIUtils.java
│   │   ├── ButtonRendererEditor.java
│   ├── com/library/db/
│   │   ├── DatabaseConnection.java
│   ├── module-info.java
├── lib/
│   ├── mysql-connector-java-8.0.27.jar
├── library_management.sql
├── README.md
```

## Database Schema
The `library_management.sql` script creates the following tables:
- **authors**: `id` (PK, auto-increment), `name`, `country`.
- **books**: `id` (PK, auto-increment), `name`, `count`, `papers`, `author`.
- **users**: `id` (PK, auto-increment), `name`, `email`.
- **students**: `id` (PK, auto-increment), `name`, `email`.
- **author_books**: `id` (PK, auto-increment), `author_id` (FK), `book_id` (FK).
- **student_books**: `id` (PK, auto-increment), `student_id` (FK), `book_id` (FK).

## Troubleshooting
- **Database Connection Error**: Verify MySQL is running, credentials in `DatabaseConnection.java` are correct, and the `library_management` database exists.
- **ClassNotFoundException**: Ensure the MySQL Connector/J JAR is in the classpath.
- **Compilation Errors**: Check JDK version (11+), module-info.java, and file paths.
- **UI Issues**: Ensure all Java files are updated as provided and rebuild the project.

## Future Enhancements
- Add search and filter functionality for tables.
- Replace `JOptionPane` with custom dialogs for a better UX.
- Implement user authentication.
- Add export/import functionality for data.

## License
This project is open-source and available under the MIT License.

---
For issues or contributions, please open an issue or submit a pull request on the repository.