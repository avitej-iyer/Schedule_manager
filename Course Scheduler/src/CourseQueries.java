/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author iyera
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CourseQueries {
    private static Connection connection;
    private static ArrayList<String> faculty = new ArrayList<String>();
    private static PreparedStatement addCourse;
    private static PreparedStatement getCourseList;
    private static PreparedStatement getCourseCodeList;
    private static PreparedStatement getSeat;
    private static int seats;
    private static ResultSet resultSet;
    private static CourseEntry courseEntry;
    private static int freeSeats;
    
    
    public static void addCourse(CourseEntry course) {
        connection = DBConnection.getConnection();
        try
        {
            addCourse = connection.prepareStatement("insert into app.course (semester, coursecode, description, seats) values (?,?,?,?)");
            addCourse.setString(1, course.getSemester());
            addCourse.setString(2, course.getCourseCode());
            addCourse.setString(3, course.getCourseDescription());
            addCourse.setInt(4, course.getSeats());
            addCourse.executeUpdate();
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
    }
    
    public static ArrayList<CourseEntry> getAllCourses(String semester) {
        
        connection = DBConnection.getConnection();
        ArrayList<CourseEntry> courses = new ArrayList<CourseEntry>();
        
        try
            {
                getCourseList = connection.prepareStatement("select coursecode,description,seats from app.course where semester = (?) order by coursecode");
                getCourseList.setString(1, semester);
                
                resultSet = getCourseList.executeQuery();
                
                while (resultSet.next()) {
                    String courseCode = resultSet.getString("courseCode");
                    String courseDescription = resultSet.getString("description");
                    Integer seats= resultSet.getInt("Seats");
                    
                    courses.add(new CourseEntry(semester, courseCode, courseDescription, seats));
                    
                }
            } 
        catch (SQLException sqlException)
            {
                sqlException.printStackTrace();
            }        
        return courses;  
    }
    
    public static ArrayList<String> getAllCourseCodes(String semester) {
        connection = DBConnection.getConnection();
        ArrayList<String> courseCodes = new ArrayList<String>();
        
        try
        {
           getCourseCodeList = connection.prepareStatement("select coursecode from app.course where semester = (?) order by coursecode");
           getCourseCodeList.setString(1, semester);
           resultSet = getCourseCodeList.executeQuery();
           
           while(resultSet.next())
            {
                courseCodes.add(resultSet.getString(1));
            }
           
        }
        catch (SQLException sqlException)
            {
                sqlException.printStackTrace();
            }  
        return courseCodes;
    }
    
    public static int getCourseSeats(String semester, String courseCode){
        
        connection = DBConnection.getConnection();
        
        try
        {
            getSeat = connection.prepareStatement("select seats from app.course where semester = ? and courseCode = ?");
            getSeat.setString(1, semester);
            getSeat.setString(2, courseCode);
            resultSet = getSeat.executeQuery();
            resultSet.next();
            seats = resultSet.getInt(1);
            
        }
        catch (SQLException sqlException)
        {
            sqlException.printStackTrace();
        }  
        return seats;
    }
    
    public static int getCourseSeatsRemaining(String semester, String courseCode) {
        connection = DBConnection.getConnection();
        
        try
        {
            int totalSeats = CourseQueries.getCourseSeats(semester, courseCode);
            
            PreparedStatement getRemainingSeats = connection.prepareStatement("select count(*) from app.schedule where semester = ? and courseCode = ?");
            getRemainingSeats.setString(1, semester);
            getRemainingSeats.setString(2, courseCode);
            resultSet = getRemainingSeats.executeQuery();
            resultSet.next();
            int remainingSeats = resultSet.getInt(1);
            
            freeSeats = totalSeats - remainingSeats;
            
        }
        catch (SQLException sqlException)
        {
            sqlException.printStackTrace();
        }  
        return freeSeats;
    }
    
    
    
}
