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
import it.polimi.tiw.beans.Report;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.ReportDAO;
import it.polimi.tiw.dao.SessionDAO;
import it.polimi.tiw.utils.DBHandler;

@WebServlet("/Verbalize")
@MultipartConfig
public class Verbalize extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public Verbalize() {
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
		
		//checking existence of the date in the course
		CDAO = new CourseDAO(connection);
		try {
			if(!CDAO.checkCourseDate(courseID).contains(date)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("User trying to verbalize grades that are not his");
				return;
			}	
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database search");
			return;
		}
		//end parameter checking
		
		SessionDAO SDAO = new SessionDAO(connection);
		ReportDAO RDAO = new ReportDAO(connection);
		int reportID = 0;
		
		Report report; 
		ArrayList<Registers> registers;
		
		try {
			reportID = RDAO.createReport(courseID,date);
			SDAO.verbalizeGrade(courseID,date,reportID);
			report = RDAO.findReport(reportID);
			registers = RDAO.findStudentData(reportID);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database search");
			return;
		}
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String json = gson.toJson(report);
		json = json + "___" + gson.toJson(registers);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);	
	}
	
}


