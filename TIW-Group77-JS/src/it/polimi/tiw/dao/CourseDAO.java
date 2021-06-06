package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Course;

public class CourseDAO {

	private Connection connection;

	public CourseDAO(Connection connection) {
		this.connection = connection;
	}

	public Course findCourse(int courseID) throws SQLException {
		Course tmp = null;
		String query = "SELECT C.ID, C.professor, C.name, P.name, P.surname FROM user AS P JOIN course AS C ON P.ID = C.professor WHERE C.ID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);			
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					tmp = new Course(result.getInt("C.ID"),result.getInt("C.professor"),result.getString("C.name"),result.getString("P.name")+" "+result.getString("P.surname"));
				}
			}
		}
		return tmp;
	}
	
	public String findName(int courseID)throws SQLException {
		String query = "SELECT name FROM course WHERE ID = ?";
		String tmp = "";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);			
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					tmp = result.getString("name");
				}
			}
		}
		return tmp;
	}
	
	public Integer checkProfID(Integer courseID) throws SQLException {
		String query="SELECT professor FROM course WHERE ID = ?";
		Integer tmp = null;
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);			
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					tmp = Integer.parseInt(result.getString("professor"));
				}
			}
		}
		return tmp;
	}
	
	public ArrayList<Integer> checkStudentID(Integer courseID, Date date) throws SQLException {
		String query="SELECT studentID FROM registers WHERE courseID = ? AND sessionDate = ?";
		ArrayList<Integer> tmp = new ArrayList<>();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);
			pstatement.setDate(2, date);	
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					tmp.add(Integer.parseInt(result.getString("studentID")));
				}
			}
		}
		return tmp;
	}
	
	public ArrayList<Integer> checkCourse(Integer profID) throws SQLException {
		String query="SELECT ID FROM course WHERE professor = ?";
		ArrayList<Integer> tmp = new ArrayList<>();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, profID);			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					tmp.add(Integer.parseInt(result.getString("ID")));
				}
			}
		}
		return tmp;
	}
	
	public ArrayList<Date> checkCourseDate(Integer courseID) throws SQLException {
		String query="SELECT sessionDate FROM registers WHERE courseID = ?";
		ArrayList<Date> tmp = new ArrayList<>();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					tmp.add(Date.valueOf(result.getString("sessionDate")));
				}
			}
		}
		return tmp;
	}
}

