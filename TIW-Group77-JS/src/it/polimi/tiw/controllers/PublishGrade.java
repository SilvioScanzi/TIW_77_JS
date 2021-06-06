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

@WebServlet("/PublishGrade")
@MultipartConfig
public class PublishGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public PublishGrade() {
		super();
	}

	public void init() throws ServletException {
		connection = DBHandler.getConnection(getServletContext());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Integer courseID = null;
		Date date = null;
		
		//checking parameters
		try {
			courseID = Integer.parseInt(request.getParameter("courseID"));
			date = Date.valueOf(request.getParameter("session"));	
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad parameter in reject");
			return;			
		}
		
		//checking date existence using course
		SessionDAO SDAO = new SessionDAO(connection);
		ArrayList<Date> dateArray = new ArrayList<>();
		try {
			dateArray = SDAO.checkDate(courseID);
			if(!dateArray.contains(date)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Date doesn't exist");
				return;	
			}	
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Failure in database search");
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
		
		if(courseID<0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Course ID can't be below 0");
			return;	
		}
		//end parameter checking
		
		SDAO = new SessionDAO(connection);
		ArrayList<Registers> registers = new ArrayList<>();
		try {
			SDAO.publishGrade(courseID,date);
			registers = SDAO.findStudentsBySession(date, courseID);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database register research");
		}
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String json = gson.toJson(registers);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);		
	}
}

