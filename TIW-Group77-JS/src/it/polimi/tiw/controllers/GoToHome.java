package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.*;
import it.polimi.tiw.dao.ProfessorDAO;
import it.polimi.tiw.dao.StudentDAO;
import it.polimi.tiw.utils.*;

@WebServlet("/GoToHome")
@MultipartConfig
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GoToHome() {
		super();
	}

	public void init() throws ServletException {
		connection = DBHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		ArrayList<Course> courses = null;

		if (user.getRole().equals("student")) {
			StudentDAO SDAO = new StudentDAO(connection);
			try {
				courses = SDAO.findCourse(user.getID());
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				response.getWriter().println("Failure in database course search");
				return;
			}
		}
		else if(user.getRole().equals("professor")) {
			ProfessorDAO PDAO = new ProfessorDAO(connection);
			try {
				courses = PDAO.findCourse(user.getID());
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				response.getWriter().println("Failure in database course research");
				return;
			}
		}
		
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		String json = gson.toJson(courses);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
}
