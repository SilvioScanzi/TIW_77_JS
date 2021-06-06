package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Registers;

public class SessionDAO {
private Connection connection; 
	
	public SessionDAO(Connection connection) {
		this.connection = connection;
	}
	
	public ArrayList<Date> findDate(int courseID) throws SQLException {
		String query = "SELECT date FROM session LEFT JOIN course ON IDcourse = ID WHERE IDCourse = ? ORDER BY date DESC";
		ArrayList<Date> tmp = new ArrayList<>();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Date date = result.getDate("date");
					tmp.add(date);
				}
			}
		}
		return tmp;
	}
	
	public ArrayList<Date> findRegisteredDate(int courseID, int studentID) throws SQLException {
		String query = "SELECT sessionDate FROM registers WHERE courseID = ? AND studentID = ? ORDER BY sessionDate DESC";
		ArrayList<Date> tmp = new ArrayList<>();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);
			pstatement.setInt(2, studentID);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Date date = result.getDate("sessionDate");
					tmp.add(date);
				}
			}
		}
		return tmp;
	}
	
	public ArrayList<Registers> findStudentsBySession(Date sessionDate, int courseID) throws SQLException{
		String query = "SELECT U.ID, U.name, U.surname, U.email, U.degree, R.grade, R.state FROM user AS U JOIN registers AS R ON U.ID=R.studentID WHERE R.sessionDate = ? AND R.courseID = ?";
	
		ArrayList<Registers> tmp = new ArrayList<>();
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setDate(1, sessionDate);
			pstatement.setInt(2, courseID);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Integer grade = result.getInt("R.grade");
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
					Registers register = new Registers(result.getInt("U.ID"),sessionDate,courseID,g,result.getString("R.state"),
							result.getString("U.name"),result.getString("U.surname"),result.getString("U.email"),result.getString("U.degree"));
					tmp.add(register);
				}
			}
		}		
		return tmp;
	}
	
	public Registers findStudentRegister(Date sessionDate, int courseID, int studentID) throws SQLException{
		String query = "SELECT U.ID, U.name, U.surname, U.email, U.degree, R.grade, R.state FROM user AS U JOIN registers AS R ON U.ID=R.studentID WHERE R.sessionDate = ? AND R.courseID = ? AND U.ID = ?";
	
		Registers tmp = null;
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setDate(1, sessionDate);
			pstatement.setInt(2, courseID);
			pstatement.setInt(3, studentID);
			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {
					Integer grade = result.getInt("R.grade");
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
					tmp = new Registers(studentID,sessionDate,courseID,g,result.getString("R.state"),
							result.getString("U.name"),result.getString("U.surname"),result.getString("U.email"),result.getString("U.degree"));					
				}
			}
		}		
		return tmp;
	} 
	
	public void updateGrade(Date sessionDate, int courseID, int studentID, int grade) throws SQLException{
		String query = "UPDATE registers SET grade = ?, state = 'entered' WHERE studentID = ? AND courseID = ? AND sessionDate = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, grade);
			pstatement.setInt(2, studentID);
			pstatement.setInt(3, courseID);
			pstatement.setDate(4, sessionDate);
			pstatement.executeUpdate();			
		}
	}
	
	public void publishGrade(int courseID, Date date) throws SQLException{
		String query = "UPDATE registers SET state = 'published' WHERE courseID = ? AND sessionDate = ? AND state = 'entered' ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);
			pstatement.setDate(2, date);
			pstatement.executeUpdate();			
		}
	}
	
	public void verbalizeGrade(int courseID, Date date,int reportID) throws SQLException{
		String query = "UPDATE registers SET grade = 2 WHERE courseID = ? AND sessionDate = ? AND state = 'rejected'";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);
			pstatement.setDate(2, date);
			pstatement.executeUpdate();			
		}
		
		query = "UPDATE registers SET state = 'verbalized', reportID = ? WHERE courseID = ? AND sessionDate = ? AND (state = 'published' OR state = 'rejected')";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, reportID);
			pstatement.setInt(2, courseID);
			pstatement.setDate(3, date);
			pstatement.executeUpdate();			
		}
	}
	
	public ArrayList<Date> checkDate(Integer courseID) throws SQLException{
		String query="SELECT sessionDate FROM registers WHERE courseID = ?";
		ArrayList<Date> tmp = new ArrayList<>();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);	
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					tmp.add(result.getDate("sessionDate"));
				}
			}
		}
		return tmp;
	}
	
	public ArrayList<Integer> checkStudent(Integer courseID, Date date) throws SQLException{
		String query="SELECT studentID FROM registers WHERE courseID = ? AND sessionDate = ?";
		ArrayList<Integer> tmp = new ArrayList<>();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);	
			pstatement.setDate(2, date);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					tmp.add(result.getInt("studentID"));
				}
			}
		}
		return tmp;
	}
}

