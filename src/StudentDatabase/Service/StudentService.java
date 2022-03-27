package StudentDatabase.Service;

import StudentDatabase.Model.Gender;
import StudentDatabase.Model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private String localHost = "jdbc:mysql://localhost:3306/student";
    private String userName = "root";
    private String passWord = "thanos15031993";
    private Connection conn = null;
    private PreparedStatement stm = null;

    public StudentService() {
        try {
            conn = DriverManager.getConnection(localHost, userName, passWord);
            System.out.println("Connection successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Student> loadData() {
        List<Student> result = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM student_database");
            while (resultSet.next()) {
                int s1 = Integer.parseInt(resultSet.getString("studentID"));
                String s2 = resultSet.getString("first_name");
                String s3 = resultSet.getString("last_name");
                Gender s4 = Gender.valueOf(resultSet.getString("gender"));
                String s5 = resultSet.getString("major");
                result.add(new Student(s1, s2, s3, s4, s5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void insertData(Student student) {
        String sqlInsert = "INSERT INTO student_database (studentID, first_name, last_name, gender, major) VALUE (?,?,?,?,?)";
        try {
            stm = conn.prepareStatement(sqlInsert);
            stm.setInt(1, student.getStudentID());
            stm.setString(2, student.getFirstName());
            stm.setString(3, student.getLastName());
            stm.setString(4, String.valueOf(student.getStuGender()));
            stm.setString(5, student.getStuMajor());
            int rowInserted = stm.executeUpdate();
            if (rowInserted > 0) {
                System.out.println("Added " + student.getFirstName() + " " + student.getLastName() + " ID: " + student.getStudentID() + " to MySQL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateData(Student student) {
        String sqlUpdate = "UPDATE student_database SET first_name = ?, last_name = ?, gender = ?, major = ? WHERE studentID = ?";
        try {
            stm = conn.prepareStatement(sqlUpdate);
            stm.setString(1, student.getFirstName());
            stm.setString(2, student.getLastName());
            stm.setString(3, String.valueOf(student.getStuGender()));
            stm.setString(4, student.getStuMajor());
            stm.setInt(5, student.getStudentID());
            int rowUpdate = stm.executeUpdate();
            if (rowUpdate > 0) {
                System.out.println("Student " + student.getFirstName() + " " + student.getLastName() + " ID: " + student.getStudentID() + " was updated to MySQL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteData(Student student) {
        String sqlDelete = "DELETE FROM student_database WHERE studentID = ?";
        try {
            stm = conn.prepareStatement(sqlDelete);
            stm.setInt(1, student.getStudentID());
            int rowDeleted = stm.executeUpdate();
            if (rowDeleted > 0) {
                System.out.println("Deleted " + student.getFirstName() + " " + student.getLastName() + " ID: " + student.getStudentID() + " from MySQL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Student> searchData(String searchFilter, String searchValue) {
        List<Student> result = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM student_database WHERE " + searchFilter + " = '" + searchValue + "'");
            while (resultSet.next()) {
                int s1 = Integer.parseInt(resultSet.getString("studentID"));
                String s2 = resultSet.getString("first_name");
                String s3 = resultSet.getString("last_name");
                Gender s4 = Gender.valueOf(resultSet.getString("gender"));
                String s5 = resultSet.getString("major");
                result.add(new Student(s1, s2, s3, s4, s5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> getSuggestData(String filter) {
        List<String> result = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT " + filter + " FROM student_database");
            while (resultSet.next()) {
                result.add(resultSet.getString(filter));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("calling inside database");
        return result;
    }

}
