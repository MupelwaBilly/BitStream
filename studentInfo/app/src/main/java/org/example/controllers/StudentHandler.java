package org.example.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.example.models.Student;

public class StudentHandler {

    // Overloaded method to add student with auto-generated ID
    public static boolean addStudent(String name, int year, String program) {
        String sql = "INSERT INTO students(name, year, program) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, year);
            pstmt.setString(3, program);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    // Overloaded method to add student with a custom user-defined ID
    public static boolean addStudent(int id, String name, int year, String program) {
        String sql = "INSERT INTO students(id, name, year, program) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, year);
            pstmt.setString(4, program);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding student with custom ID: " + e.getMessage());
            return false;
        }
    }

    public static List<Student> getAllStudents() {
        String sql = "SELECT id, name, year, program FROM students";
        List<Student> studentsList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                studentsList.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("year"),
                        rs.getString("program")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students: " + e.getMessage());
        }
        return studentsList;
    }

    public static Student getStudentById(int studentId) {
        String sql = "SELECT id, name, year, program FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("year"),
                            rs.getString("program")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding student: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateStudent(int currentId, int newId, String name, int year, String program) {
        String sql = "UPDATE students SET id = ?, name = ?, year = ?, program = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newId);
            pstmt.setString(2, name);
            pstmt.setInt(3, year);
            pstmt.setString(4, program);
            pstmt.setInt(5, currentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

    public static int getStudentCount() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching count: " + e.getMessage());
        }
        return 0;
    }
}