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


public class StudentQueries {
    
    private static Connection connection; 
    private static ArrayList<String> faculty = new ArrayList<String>(); 
    private static PreparedStatement addStudent;
    private static PreparedStatement getStudentList;
    private static PreparedStatement getStudentID;
    private static PreparedStatement getStudentNames;
    private static PreparedStatement getStudentName;
    private static PreparedStatement getCourseCodeList;
    private static PreparedStatement updateWaitlistedStudent;
    private static PreparedStatement dropStudent;
    private static int seats;
    private static ResultSet resultSet;
    private static String studentID;
    private static CourseEntry courseEntry;
    private static String firstName;
    private static String lastName;
    
    
    public static void addStudent(StudentEntry student){
        connection = DBConnection.getConnection();
        
        try
        {
            addStudent = connection.prepareStatement("insert into app.student (studentid, firstname, lastname) values (?,?,?)");
            addStudent.setString(1, student.getStudentID());
            addStudent.setString(2, student.getFirstName());
            addStudent.setString(3, student.getLastName());
            addStudent.executeUpdate();
            
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
    }
    
    public static ArrayList<StudentEntry> getAllStudents() {
        connection = DBConnection.getConnection();
        ArrayList<StudentEntry> students = new ArrayList<StudentEntry>();
        
        try
        {
            getStudentList = connection.prepareStatement("select * from app.student order by studentid");
            resultSet = getStudentList.executeQuery();
            while (resultSet.next()) {
                    String studentID = resultSet.getString("studentID");
                    String firstName = resultSet.getString("FirstName");
                    String lastName = resultSet.getString("LastName");
                    
                    students.add(new StudentEntry(studentID, firstName, lastName));
                    
                }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return students;
    }
    
    public static String getStudentID(String firstName, String lastName){
        connection = DBConnection.getConnection();
        String ID = new String();
        
        try
        {
            getStudentID = connection.prepareStatement("select studentid from app.student where firstname = ? and lastname = ?");
            getStudentID.setString(1, firstName);
            getStudentID.setString(2, lastName);
            resultSet = getStudentID.executeQuery();
            
            while (resultSet.next()){
                ID = resultSet.getString("studentid");
            }
            
            
            
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return ID;
    }
    
    public static ArrayList<String> getStudentNames(){
        connection = DBConnection.getConnection();
        ArrayList<String> studentNames = new ArrayList<String>();
        
        try
        {
            getStudentNames = connection.prepareStatement("select firstname, lastname from app.student");
            resultSet = getStudentNames.executeQuery();
            
            while (resultSet.next()) {
                    String firstName = resultSet.getString("FirstName");
                    String lastName = resultSet.getString("LastName");
                    
                    studentNames.add(firstName + " " + lastName);
                    
                }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return studentNames;
    }
    
    public static String studentNameToID(String studentFullName){
        String[] split_name = studentFullName.split(" ");
        String firstName = split_name[0];
        String lastName = split_name[1];
        connection = DBConnection.getConnection();
        
        try
        {
            getStudentNames = connection.prepareStatement("select studentid from app.student where firstname = ? and lastname = ?");
            getStudentNames.setString(1, firstName);
            getStudentNames.setString(2, lastName);
            resultSet = getStudentNames.executeQuery();
            
            while(resultSet.next()){
            studentID = resultSet.getString("studentid");
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return studentID;
    }
    
    public static String studentIDToName(String studentID){
        connection = DBConnection.getConnection();
        
        try
        {
           getStudentName =  connection.prepareStatement("select firstname, lastname from app.student where studentid = ?");
           getStudentName.setString(1, studentID);
           resultSet = getStudentName.executeQuery();
           
           while(resultSet.next())
           {
           studentID = resultSet.getString("firstname") + " " + resultSet.getString("lastname");
           }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
       return studentID; 
    }
    
    public static ArrayList<ArrayList<String>> displayCourseStudentListQuery(String currentSemester, String courseCode){
        ArrayList<ArrayList<String>> displayList = new ArrayList<ArrayList<String>>();
        
        connection = DBConnection.getConnection();
        
        try
        {
            
             getStudentList = connection.prepareStatement("SELECT DISTINCT STUDENTID, STATUS, TIMESTAMP FROM APP.COURSE INNER JOIN APP.SCHEDULE ON APP.COURSE.SEMESTER = APP.SCHEDULE.SEMESTER WHERE APP.COURSE.SEMESTER = ? and APP.SCHEDULE.COURSECODE = ? ORDER BY APP.SCHEDULE.STATUS, APP.SCHEDULE.TIMESTAMP");
             getStudentList.setString(1, currentSemester);
             getStudentList.setString(2, courseCode);
             
             resultSet = getStudentList.executeQuery();
             
             while(resultSet.next()){
                 ArrayList<String> studentInfo = new ArrayList<String>();
                 studentInfo.add(resultSet.getString("studentID"));
                 studentInfo.add(resultSet.getString("status"));
                 displayList.add(studentInfo);
                 
             }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return displayList;
    }
    
    
    
    public static String dropStudentFromCourse(String curSemester, String StudentID, String CourseID){
        String changesMade = new String();
        connection = DBConnection.getConnection();
        
        try
        {
            if (ScheduleQueries.studentWaitlistStatus(curSemester, StudentID, CourseID) == "W"){
                
                dropStudent = connection.prepareStatement("delete from app.schedule where studentid = ? and semester = ?");
                dropStudent.setString(1, StudentID);
                dropStudent.setString(2, curSemester);
                dropStudent.executeUpdate();
            }
            
            else{
                String curStudentName = StudentQueries.studentIDToName(StudentID);
                String updatedStudentName = new String();
                
                updateWaitlistedStudent = connection.prepareStatement("SELECT STUDENTID FROM APP.SCHEDULE WHERE COURSECODE = ? AND SEMESTER = ? AND STATUS = 'W' ORDER BY TIMESTAMP FETCH FIRST 1 ROWS ONLY");
                updateWaitlistedStudent.setString(1, CourseID);
                updateWaitlistedStudent.setString(2, curSemester);
                resultSet = updateWaitlistedStudent.executeQuery();
                while (resultSet.next()){
                    updatedStudentName = StudentQueries.studentIDToName(resultSet.getString("StudentID"));
                }
                
                updateWaitlistedStudent = connection.prepareStatement("UPDATE APP.SCHEDULE SET STATUS = 'S' WHERE TIMESTAMP = (SELECT TIMESTAMP FROM APP.SCHEDULE WHERE COURSECODE = ? AND SEMESTER = ? AND STATUS = 'W' ORDER BY TIMESTAMP FETCH FIRST 1 ROWS ONLY)");
                updateWaitlistedStudent.setString(1, CourseID);
                updateWaitlistedStudent.setString(2, curSemester);
                updateWaitlistedStudent.executeUpdate();
                
                dropStudent = connection.prepareStatement("delete from app.schedule where studentid = ? and semester = ?");
                dropStudent.setString(1, StudentID);
                dropStudent.setString(2, curSemester);
                dropStudent.executeUpdate();
                
                /*
                dropStudent = connection.prepareStatement("delete from app.student where studentid = ?");
                dropStudent.setString(1, StudentID);
                dropStudent.executeUpdate(); 
                */
                
                if (updatedStudentName.isEmpty()){
                    changesMade = CourseID + " : " + curStudentName + " was removed.";  
                }
                else{
                    changesMade = CourseID + " : " + updatedStudentName + " was scheduled from waitlist, " + curStudentName + " was removed."; 
                }
                   
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return changesMade;
    }
    
    public static void dropStudentFromRoster(String StudentID){
        connection = DBConnection.getConnection();
        
        try
        {
            dropStudent = connection.prepareStatement("delete from app.student where studentid = ?");
            dropStudent.setString(1, StudentID);
            dropStudent.executeUpdate(); 
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
       
    }
}
