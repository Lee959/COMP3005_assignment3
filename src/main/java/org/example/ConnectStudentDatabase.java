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

        // Try to connect to the database and execute the query, if error occurs, print the error message
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
     */
    public static boolean addStudent(String firstName, String lastName, String email, LocalDate enrollmentDate) {
        String query = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setDate(4, Date.valueOf(enrollmentDate));

            int rowsAffect = pstmt.executeUpdate();
            if (rowsAffect > 0) {
                System.out.println("Student added successfully: " + firstName + " " + lastName);
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




    public static void main(String[] args) {

        try {
            Class.forName("org.postgresql.Driver");
            getAllStudents();

        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found");
            System.err.println("Make sure postgresql.jar is in your classpath");
            e.printStackTrace();
        }

    }
}