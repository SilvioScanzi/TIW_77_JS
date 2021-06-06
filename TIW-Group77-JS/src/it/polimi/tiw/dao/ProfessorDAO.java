package it.polimi.tiw.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import it.polimi.tiw.beans.Course;

public class ProfessorDAO {
	private Connection connection; 
	
	public ProfessorDAO(Connection connection) {
		this.connection = connection;
	}
	
	public ArrayList<Course> findCourse(int professorID) throws SQLException {
		String query = "SELECT C.ID, C.professor, C.name, U.name, U.surname FROM course AS C JOIN user AS U ON C.professor = U.ID WHERE C.professor = ? ORDER BY C.name";
		ArrayList<Course> tmp = new ArrayList<>();
		SessionDAO SDAO = new SessionDAO(connection);
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, professorID);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Course course = new Course(result.getInt("C.ID"),result.getInt("C.professor"),result.getString("C.name"),result.getString("U.name")+" "+result.getString("U.surname"));
					course.setSession(SDAO.findDate(course.getID()));
					tmp.add(course);
				}
			}
		}
		return tmp;
	}
}
