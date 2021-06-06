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

@WebServlet("/RegisteredStudents")
@MultipartConfig
public class RegisteredStudents extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public RegisteredStudents() {
		super();
	}

	public void init() throws ServletException {
		connection = DBHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Integer courseID = null;
		Date date = null;

		SessionDAO SDAO = new SessionDAO(connection);
		CourseDAO CDAO = new CourseDAO(connection);
		ReportDAO RDAO = new ReportDAO(connection);
		ArrayList<Registers> registers = new ArrayList<>();
		String course = null;
		ArrayList<Report> reports;

		// checking parameters
		try {
			courseID = Integer.parseInt(request.getParameter("courseID"));
			date = Date.valueOf(request.getParameter("session"));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad parameter in registered students");
			return;
		}
		
		
		// checking if user is the professor holding the course
		try {
			if (!CDAO.checkProfID(courseID).equals((Integer) user.getID())) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("User trying to access courses which are not his");
				return;
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database search");
			return;
		}
		// end parameter checking

		try {
			registers = SDAO.findStudentsBySession(date, courseID);
			course = CDAO.findName(courseID);
			reports = RDAO.findAllReports(courseID,date);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database students research");
			return;
		}

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String json = gson.toJson(registers);
		json = json + "___" + gson.toJson(course);
		json = json + "___" + gson.toJson(reports);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
