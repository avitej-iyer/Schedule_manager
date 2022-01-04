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
import java.sql.Timestamp;



public class ScheduleQueries {
    
    private static Connection connection;
    private static PreparedStatement addScheduleEntry;
    private static PreparedStatement getStudentSchedule;
    private static PreparedStatement getScheduledStudentCount;
    private static ResultSet resultSet;
    private static int scheduledSeats;
    private static PreparedStatement getStudentStatus;
    
    
    
    public static void addScheduleEntry(ScheduleEntry entry){
        connection = DBConnection.getConnection();
        
        try
        {
            addScheduleEntry = connection.prepareStatement("insert into app.schedule (semester, studentid, coursecode, status, timestamp) values (?,?,?,?,?)");
            addScheduleEntry.setString(1, entry.getSemester());
            addScheduleEntry.setString(2, entry.getStudentID());
            addScheduleEntry.setString(3, entry.getCourseCode());
            addScheduleEntry.setString(4, entry.getStatus());
            addScheduleEntry.setTimestamp(5, entry.getTimeStamp());
            
            addScheduleEntry.executeUpdate();     
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
    }

    public static ArrayList<ScheduleEntry> getScheduleByStudent(String studentFullName, String semester_name){
        String studentID = StudentQueries.studentNameToID(studentFullName);
        connection = DBConnection.getConnection();
        
        ArrayList<ScheduleEntry> schedules = new ArrayList<ScheduleEntry>();
        
        try
        {
            getStudentSchedule = connection.prepareStatement("select * from app.schedule where studentid = ? and semester = ?");
            getStudentSchedule.setString(1, studentID);
            getStudentSchedule.setString(2, semester_name);
            
            resultSet = getStudentSchedule.executeQuery();
            
            while (resultSet.next()){
                String semester = resultSet.getString("semester");
                String courseCode = resultSet.getString("courseCode");
                String studentIDString = resultSet.getString("StudentID");
                String status = resultSet.getString("status");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                
                schedules.add(new ScheduleEntry(semester, courseCode,studentIDString, status, timestamp));
            }
            
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return schedules;
    }
    
    public static int getScheduledStudentCount(String semester, String courseCode){
        connection = DBConnection.getConnection();
        try
        {
           getScheduledStudentCount = connection.prepareStatement("select count(student) from app.schedule where semester = ? and coursecode = ?");
           getScheduledStudentCount.setString(1, semester);
           getScheduledStudentCount.setString(2, courseCode);
           
           resultSet = getScheduledStudentCount.executeQuery();
           
           resultSet.next();
           
           scheduledSeats = resultSet.getInt(1);
           
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return scheduledSeats;
    }
    
    public static ArrayList<String> getEnrolledCourses(String studentID, String curSemester){
        String studentName = StudentQueries.studentIDToName(studentID);
        ArrayList<ScheduleEntry> schedules = new ArrayList<ScheduleEntry>();
        ArrayList<String> enrolledCourses = new ArrayList<String>();
        
        schedules = getScheduleByStudent(studentName, curSemester);
        
        for (int i=0;i<schedules.size(); i++){
            enrolledCourses.add((schedules.get(i)).getCourseCode());
        }
        
        return enrolledCourses; 
    }
    
    public static String studentWaitlistStatus(String curSemester, String studentID, String courseCode){
        connection = DBConnection.getConnection();
        String status = "";
        try
        {
            getStudentStatus = connection.prepareStatement("select status from app.schedule where semester = ? and coursecode = ? and studentid = ?");
            getStudentStatus.setString(1, curSemester);
            getStudentStatus.setString(2, courseCode);
            getStudentStatus.setString(3, studentID);
            
            resultSet = getStudentStatus.executeQuery();
            
            while (resultSet.next()){
                if (resultSet.getString("status") == "W"){
                    status = "W";
                }
                else{
                    status = "S";
                }
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return status;
    }
    
}
