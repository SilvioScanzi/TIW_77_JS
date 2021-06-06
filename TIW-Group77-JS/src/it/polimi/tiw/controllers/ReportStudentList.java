package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
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
import it.polimi.tiw.dao.ReportDAO;
import it.polimi.tiw.utils.DBHandler;

@WebServlet("/ReportStudentList")
@MultipartConfig
public class ReportStudentList extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public ReportStudentList() {
		super();
	}

	public void init() throws ServletException {
		connection = DBHandler.getConnection(getServletContext());
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Integer reportID = null;
		
		ReportDAO RDAO = new ReportDAO(connection);
		
		//checking parameters
		try{
			reportID = Integer.parseInt(request.getParameter("reportID"));
		}catch(Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad parameter in showReport");
			return;
		}
		
		// checking if user is the professor holding the course for which the report is requested and if the report exists
		try {
			if(!RDAO.checkProfReport(reportID, user.getID())) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("User is trying to access a report which is not his");
				return;
			}
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database report research");
			return;
		}		
		//end parameter checking
		
		ArrayList<Registers> registers = null;
		
		try {
			registers = RDAO.findStudentData(reportID);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Failure in database register research");
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String json = gson.toJson(registers);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
	
}
