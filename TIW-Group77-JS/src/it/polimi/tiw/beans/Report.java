package it.polimi.tiw.beans;
import java.sql.Time;
import java.sql.Date;
public class Report {
	private int ID;
	private Date date;
	private Time time;
	private Date dateSession;
	private int IDCourse;
	
	public Report(int iD, Date date, Time time, Date dateSession, int iDCourse) {
		ID = iD;
		this.date = date;
		this.time = time;
		this.dateSession = dateSession;
		IDCourse = iDCourse;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Date getDateSession() {
		return dateSession;
	}

	public void setDateSession(Date dateSession) {
		this.dateSession = dateSession;
	}

	public int getIDCourse() {
		return IDCourse;
	}

	public void setIDCourse(int iDCourse) {
		IDCourse = iDCourse;
	}
}
