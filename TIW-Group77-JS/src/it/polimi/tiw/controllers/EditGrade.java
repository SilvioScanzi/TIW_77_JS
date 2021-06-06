package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Registers;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.SessionDAO;
import it.polimi.tiw.utils.DBHandler;

@WebServlet("/EditGrade")
@MultipartConfig
public class EditGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public EditGrade() {
		super();
	}

	public void init() throws ServletException {
		connection = DBHandler.getConnection(getServletContext());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Integer courseID = null;
		Integer studentID = null ;
		Date date = null;
		Integer grade = null;
		
		try {
			courseID = Integer.parseInt(request.getParameter("courseID"));
			studentID = Integer.parseInt(request.getParameter("studentID"));
			date = Date.valueOf(request.getParameter("session"));
			grade = Integer.parseInt(request.getParameter("grade"));
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad parameter in edit grade");
			return;	
		}

		//checking user is the professor holding the course
		CourseDAO CDAO = new CourseDAO(connection);
		try {
			if(!CDAO.checkProfID(courseID).equals((Integer)user.getID())) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("User trying to modify grades that are not his");
				return;
			}	
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database search");
			return;
		}
		
		//checking student registered to selected session and course (if date is incorrect, no student is returned -> error)
		SessionDAO SDAO= new SessionDAO(connection);
		ArrayList<Integer> studentArray = new ArrayList<>();
		try {
			studentArray = SDAO.checkStudent(courseID,date);
			if(!studentArray.contains(studentID)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Trying to modify the grade of a not registered student");
				return;
			}	
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database search");
			return;
		}
		
		//checking grade is correct
		if(!((grade>=1 && grade<=3) || (grade>=18 && grade<=31))) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Grade " + grade + " is invalid");
			return;
		}
		//end parameter checking
		
		
		Registers r;
		SDAO = new SessionDAO(connection);
		try {
			SDAO.updateGrade(date, courseID, studentID, grade);
			r = SDAO.findStudentRegister(date, courseID, studentID);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database register research");
			return;
		}
		
		
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String json = gson.toJson(r);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
}

