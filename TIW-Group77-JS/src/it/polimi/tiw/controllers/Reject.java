package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.StudentDAO;
import it.polimi.tiw.utils.DBHandler;

@WebServlet("/Reject")
@MultipartConfig
public class Reject extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public Reject() {
		super();
	}	
	
	public void init() throws ServletException {
		connection = DBHandler.getConnection(getServletContext());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Integer courseID = null;
		Date date = null;
		
		//checking parameters
		try {			
			courseID = Integer.parseInt(request.getParameter("courseID"));	
			String stringDate = request.getParameter("session");
			date = Date.valueOf(stringDate);
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad parameter in reject");
			return;
		}
		
		//checking user is the student enrolled to the session in that date (so it's also a check for the correct date and courseID)
		CourseDAO CDAO = new CourseDAO(connection);
		try {
			if(!CDAO.checkStudentID(courseID,date).contains((Integer)user.getID())) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("User trying to reject grades that are not his");
				return;
			}	
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database search");
			return;
		}
		//end parameter checking
		
		StudentDAO SDAO = new StudentDAO(connection);
		
		try {
			SDAO.rejectGrade(user.getID(), courseID, date);
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database update");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);	
		response.getWriter().println("The grade has been rejected");
	}
}


