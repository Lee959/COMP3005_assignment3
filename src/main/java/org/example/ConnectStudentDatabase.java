package org.example;
import java.sql.*;
import java.time.LocalDate;

public class ConnectStudentDatabase {
    // Database connection parameters
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/students_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "zhuixingren";

    /**
     * Try to connect the database, throws SQLException Error
     * @return  Connection Object
     * @throws SQLException
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Retrieves and displays all student records from the database
     */
    public static void getAllStudents() {
        // SQL query to retrieve all student records
        String query = "SELECT student_id, first_name, last_name, email, enrollment_date FROM students";

        // Try to connect to the database, and create a statement, and execute the query
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // while there are more records, print each record, and format the output
            while (rs.next()) {
                int id = rs.getInt("student_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                Date enrollmentDate = rs.getDate("enrollment_date");
                System.out.printf("%d | %s | %s | %s | %s%n", id, firstName, lastName, email, enrollmentDate);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Adds a new student record to the database
     * @param firstName
     * @param lastName
     * @param email
     * @param enrollmentDate
     * @return true if add successful
     */
    public static boolean addStudent(String firstName, String lastName, String email, LocalDate enrollmentDate) {
        String query = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?)";

        // Try to connect to the database and execute the query
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            // set parameter values for the prepared statement
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setDate(4, Date.valueOf(enrollmentDate));

            // exctue the insert operation
            int rowsAffect = pstmt.executeUpdate();
            if (rowsAffect > 0) {
                System.out.printf("Student added successfully: %s, %s Email: %s EnrollmentDate: %s%n", firstName, lastName, email, enrollmentDate);
                return true;
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.err.println("Error: Email already exists in database");
            } else {
                System.err.println("Error adding student: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Updates the email address for a specific student
     * @param studentId
     * @param newEmail
     * @return true if updated successful
     */

    public static boolean updateStudentEmail(int studentId, String newEmail) {
        String query = "UPDATE students SET email = ? WHERE student_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // set parameter value
            pstmt.setString(1, newEmail);
            pstmt.setInt(2, studentId);
            int rowsAffect = pstmt.executeUpdate();

            if (rowsAffect > 0) {
                System.out.println("Email updated successfully for student ID: " + studentId);
                return true;
            } else {
                System.out.println("No student found with ID: " + studentId);
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.err.println("Error: Email already exists in database");
            } else {
                System.err.println("Error adding student: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Deletes the record of the student with the specified student_id.
     * @param studentId
     * @return
     */
    public static boolean deleteStudent(int studentId) {
        String query = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // set parameter value
            pstmt.setInt(1, studentId);
            int rowsAffect = pstmt.executeUpdate();

            if (rowsAffect > 0) {
                System.out.println("Student deleted successfully (ID: " + studentId + ")");
                return true;
            } else {
                System.out.println("No student found with ID: " + studentId);
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.err.println("Error: Email already exists in database");
            } else {
                System.err.println("Error adding student: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }




    public static void main(String[] args) {
        try {
            // Load PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");

//             1. Display all students
            System.out.println("All Students:");
            getAllStudents();


//             2. Add a new student
            System.out.println("\nAdding a new student:");
            addStudent("Kristen", "Leee", "KristenLeeee@tester.com", LocalDate.now());
            System.out.println("All Students after addition:");
            getAllStudents();


//             3. Update a student's email
            System.out.println("\nUpdating student email:");
            updateStudentEmail(1, "john.doe.updated@example.com");
            System.out.println("All Students after email update:");
            getAllStudents();


//              4. Delete a student
            System.out.println("\nDeleting a student:");
            deleteStudent(4);
            System.out.println("All Students after deletion:");
            getAllStudents();

        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found");
            System.err.println("Make sure postgresql.jar is in your classpath");
            e.printStackTrace();
        }

    }
}