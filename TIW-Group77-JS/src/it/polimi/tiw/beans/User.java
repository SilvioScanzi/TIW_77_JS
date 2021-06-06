package it.polimi.tiw.beans;

public class User {
	private int ID;	
	private String role;
	private String name;
	private String surname;
	private String email;
	private String degree;
	
	//Constructor used for professor
	public User(int iD, String role, String name, String surname) {
		ID = iD;
		this.role = role;
		this.name = name;
		this.surname = surname;
	}
	
	//Constructor used for student
	public User(int iD, String role, String name, String surname, String email, String degree) {
		ID = iD;
		this.role = role;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.degree = degree;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}
	
	
}
