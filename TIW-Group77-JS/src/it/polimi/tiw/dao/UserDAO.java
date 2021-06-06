package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;

public class UserDAO {
private Connection connection; 
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public User checkCredentials(String id, String pwd) throws SQLException {
		String query = "SELECT ID, role, name, surname, email, degree FROM user  WHERE ID = ? AND password =?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, id);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User(result.getInt("id"),result.getString("role"),result.getString("name"),result.getString("surname"),
							result.getString("email"),result.getString("degree"));
					return user;
				}
			}
		}
	}
}
