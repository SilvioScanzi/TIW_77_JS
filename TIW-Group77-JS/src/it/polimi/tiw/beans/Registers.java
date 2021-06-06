package it.polimi.tiw.beans;
import java.util.Date;

public class Registers {
	private int studentID; 
	private Date sessionDate;
	private int courseID; 
	private String grade;
	private String state;
	//Used for professor side
	private String studentName;
	private String studentSurname;
	private String studentMail;
	private String studentDegree;
	
	public Registers(int studentID, Date sessionDate, int courseID, String grade, String state) {
		this.studentID = studentID;
		this.sessionDate = sessionDate;
		this.courseID = courseID;
		this.grade = grade;
		this.state = state;
	}
	
	public Registers(int studentID, Date sessionDate, int courseID, String grade, String state, String studentName, String studentSurname, String studentMail, String studentDegree) {
		this(studentID,sessionDate,courseID,grade,state);
		this.studentName = studentName;
		this.studentSurname = studentSurname;
		this.studentMail = studentMail;
		this.studentDegree = studentDegree;
	}
	
	public Registers(int ID, String name, String surname, String grade) {
		studentID = ID;
		studentName = name;
		studentSurname = surname;
		this.grade = grade;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentSurname() {
		return studentSurname;
	}

	public void setStudentSurname(String studentSurname) {
		this.studentSurname = studentSurname;
	}

	public String getStudentMail() {
		return studentMail;
	}

	public void setStudentMail(String studentMail) {
		this.studentMail = studentMail;
	}

	public String getStudentDegree() {
		return studentDegree;
	}

	public void setStudentDegree(String studentDegree) {
		this.studentDegree = studentDegree;
	}

	public int getStudentID() {
		return studentID;
	}

	public void setStudentID(int studentID) {
		this.studentID = studentID;
	}

	public Date getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}	
}
