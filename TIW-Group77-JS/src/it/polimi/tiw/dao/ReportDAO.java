package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

import it.polimi.tiw.beans.Registers;
import it.polimi.tiw.beans.Report;

public class ReportDAO {
private Connection connection; 
	
	public ReportDAO(Connection connection) {
		this.connection = connection;
	}
	
	public int createReport(int courseID,Date sessionDate) throws SQLException {
		String query = "INSERT into report (date, time, sessionDate, IDcourse) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setDate(1, new Date(System.currentTimeMillis()));
			pstatement.setTime(2, new Time(System.currentTimeMillis()));
			pstatement.setDate(3, sessionDate);
			pstatement.setInt(4, courseID);
			pstatement.executeUpdate();			
		}
		
		query = "SELECT MAX(ID) FROM report WHERE IDcourse = ? AND sessionDate = ?";
		int tmp = 0;
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);
			pstatement.setDate(2, sessionDate);
			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {					
					tmp = result.getInt("MAX(ID)");					
				}
			}
		}
		return tmp;
	}
	
	public Report findReport(int reportID) throws SQLException {
		String query = "SELECT * FROM report WHERE ID = ?";
		Report tmp = null;
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, reportID);
			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {					
					tmp = new Report(reportID,result.getDate("date"),result.getTime("time"),result.getDate("sessionDate"),result.getInt("IDcourse"));					
				}
			}		
		}
		return tmp;
	}
	
	public ArrayList<Report> findAllReports(int courseID,Date date) throws SQLException {
		String query = "SELECT * FROM report WHERE IDcourse = ? AND sessionDate = ?";
		ArrayList<Report> tmp = new ArrayList<>();
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseID);
			pstatement.setDate(2, date);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					tmp.add(new Report(result.getInt("ID"),result.getDate("date"),result.getTime("time"),result.getDate("sessionDate"),courseID));
				}
			}
		}
		
		return tmp;		
	}
	
	public boolean checkProfReport(int reportID, int professorID) throws SQLException {
		String query = "SELECT * FROM report AS R JOIN course AS C ON R.IDcourse = C.ID JOIN user AS U ON U.ID = C.professor WHERE R.ID = ? AND U.ID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, reportID);		
			pstatement.setInt(2, professorID);		
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) return true;
				else return false;
			}
		}
	}
	
	public ArrayList<Registers> findStudentData(int reportID) throws SQLException {
		String query = "SELECT U.ID, U.name, U.surname, B.grade FROM report AS A JOIN registers AS B ON A.ID = B.reportID JOIN user AS U ON B.studentID = U.ID WHERE A.ID = ?";
		ArrayList<Registers> tmp = new ArrayList<>();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, reportID);		
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Integer grade = result.getInt("B.grade");
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
					tmp.add(new Registers(result.getInt("U.ID"),result.getString("U.name"),result.getString("U.surname"),g));
				}
			}
		}
		return tmp;
	}
}

