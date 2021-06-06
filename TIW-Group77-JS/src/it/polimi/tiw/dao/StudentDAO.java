package it.polimi.tiw.dao;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.Registers;

public class StudentDAO {
	private Connection connection; 
	
	public StudentDAO(Connection connection) {
		this.connection = connection;
	}
	
	public ArrayList<Course> findCourse(int studentID) throws SQLException {
		String query = "SELECT C.ID, C.professor, C.name, P.name, P.surname FROM user AS P JOIN course AS C ON P.ID = C.professor LEFT JOIN enrolls AS E ON E.courseID = C.ID  WHERE E.studentID = ? ORDER BY C.name";
		ArrayList<Course> tmp = new ArrayList<>();
		SessionDAO SDAO = new SessionDAO(connection);
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, studentID);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Course course = new Course(result.getInt("C.ID"),result.getInt("C.professor"),result.getString("C.name"),result.getString("P.name")+" "+result.getString("P.surname"));
					course.setSession(SDAO.findRegisteredDate(course.getID(),studentID));
					tmp.add(course);
				}
			}
		}
		return tmp;
	}

	public Registers findRegister(int studentID, int courseID, Date date) throws SQLException{
		Registers tmp = null;
		String query = "SELECT grade, state FROM registers WHERE studentID = ? AND courseID = ? AND sessionDate = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, studentID);
			pstatement.setInt(2, courseID);
			pstatement.setDate(3, date);
			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {
					Integer grade = result.getInt("grade");
					String g;
					if(grade==0) {
						g = null;
					}
					else if(grade==1) {
						g = "absent";
					}
					else if(grade==2) {
						g = "failing grade";
					}
					else if(grade==3) {
						g = "skip next session";
					}
					else if(grade==31) {
						g = "30 with honors";
					}
					else g = grade.toString();					
					tmp = new Registers(studentID,date,courseID,g,result.getString("state"));
				}
			}
		}
		return tmp;
	}
	
	public void rejectGrade(int studentID, int courseID, Date date) throws SQLException{
		String query = "UPDATE registers SET state = 'rejected' WHERE studentID = ? AND courseID = ? AND sessionDate = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, studentID);
			pstatement.setInt(2, courseID);
			pstatement.setDate(3, date);
			pstatement.executeUpdate();			
		}
	}
}
