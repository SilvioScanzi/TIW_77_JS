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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.polimi.tiw.beans.Registers;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.SessionDAO;
import it.polimi.tiw.utils.DBHandler;

@WebServlet("/EditMultipleGrade")
@MultipartConfig
public class EditMultipleGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public EditMultipleGrade() {
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
		String jsonString = null;
		
		ArrayList<Integer> studentID = new ArrayList<>();
		ArrayList<Integer> studentGrade = new ArrayList<>();
		
		SessionDAO SDAO = new SessionDAO(connection);
		
		try {
			courseID = Integer.parseInt(request.getParameter("courseID"));
			date = Date.valueOf(request.getParameter("session"));
			jsonString = request.getParameter("studentgrades");
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad parameter in edit grade");
			return;	
		}
		
		Gson gson =  new GsonBuilder().create();
		JsonArray js = gson.fromJson(jsonString, JsonArray.class);
		
		if(js.size()%2==1) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad parameter in edit grade");
			return;	
		}
		
		for (int i = 0; i < js.size(); i++){
		    String val = js.get(i).toString();
		    try {
		    	char c = val.charAt(1);
		    	int v = Integer.parseInt(String.valueOf(c));
		    	if(i%2==0) {
		    		Registers r = SDAO.findStudentRegister(date,courseID,v);
		    		if(r==null || !r.getState().equals("not entered")) {
		    			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		    			response.getWriter().println("Student is not registered in this session");
		    			return;	
		    		}		   
		    		else {
		    			studentID.add(v);
		    		}
		    	}
		    	else {
		    		if((1<=v && v<=3) || (18<=v && v<=31)) {
		    			studentGrade.add(v);
		    		}
		    		else {
		    			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		    			response.getWriter().println("Invalid grade");
		    			return;	
		    		}
		    	}
		    }
		    catch(Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				response.getWriter().println("Failure in database search");
				return;	
		    }
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
		//end parameter checking
		
		
		try {
			for(int i=0;i<studentID.size();i++) {
				SDAO.updateGrade(date, courseID, studentID.get(i), studentGrade.get(i));
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Failure in database register research");
			return;
		}
	
		response.setStatus(HttpServletResponse.SC_OK);	
		response.getWriter().println("The grades have been updated");
	}
}

