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

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.Registers;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.SessionDAO;
import it.polimi.tiw.dao.StudentDAO;
import it.polimi.tiw.utils.DBHandler;

@WebServlet("/SessionResult")
@MultipartConfig
public class SessionResult extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public SessionResult() {
		super();
	}
	
	public void init() throws ServletException {
		connection = DBHandler.getConnection(getServletContext());
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		Registers register = null;
		Course course = null;
		Integer courseID = null;
		Date date = null;
	
		StudentDAO SDAO = new StudentDAO(connection);
		CourseDAO CDAO = new CourseDAO(connection);
		
		//parameter checking
		try {
			courseID = Integer.parseInt(request.getParameter("courseID"));
			String stringDate = request.getParameter("session");
			date = Date.valueOf(stringDate);			
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad parameter in session result");
			return;
		}
		
		//checking date existence using course
		SessionDAO SeDAO = new SessionDAO(connection);
		ArrayList<Date> dateArray = new ArrayList<>();
		try {
			dateArray = SeDAO.checkDate(courseID);
			if(!dateArray.contains(date)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Date doesn't exist");
				return;
			}	
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database search");
			return;
		}

		//checking if student is registered in the session
		CDAO = new CourseDAO(connection);
		try {
			if(!CDAO.checkStudentID(courseID,date).contains((Integer)user.getID())){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("User trying to view a session result that is not his");
				return;
			}	
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database search");
			return;	
		}

		if(courseID<0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("CourseID can't be below 0");
			return;
		}
		
		//end parameter checking
		
		try {
			register = SDAO.findRegister(user.getID(), courseID, date);
			course = CDAO.findCourse(courseID);
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database session search");
			return;
		}
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
		String json = gson.toJson(course);
		json = json + "___" + gson.toJson(register);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");		
		response.getWriter().write(json);		
	}
}

