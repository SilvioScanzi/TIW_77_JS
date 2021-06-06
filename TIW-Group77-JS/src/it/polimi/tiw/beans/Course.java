package it.polimi.tiw.beans;

import java.sql.Date;
import java.util.ArrayList;

public class Course {
	private int ID;
	private int profID;
	private String name;
	private String profName;
	private ArrayList<Date> session;
	
	public Course(int iD, int profID, String name, String profName) {
		ID = iD;
		this.profID = profID;
		this.name = name;
		this.profName = profName;
		this.session = null;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getProfID() {
		return profID;
	}

	public void setProfID(int profID) {
		this.profID = profID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfName() {
		return profName;
	}

	public void setProfName(String profName) {
		this.profName = profName;
	}
	
	public ArrayList<Date> getSession() {
		return session;
	}

	public void setSession(ArrayList<Date> session) {
		this.session = session;
	}
}
