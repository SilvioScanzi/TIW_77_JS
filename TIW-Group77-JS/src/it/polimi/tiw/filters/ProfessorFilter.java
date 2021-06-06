package it.polimi.tiw.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProfessorFilter implements Filter {

	public ProfessorFilter() {}

	
	public void destroy() {}

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath();

		HttpSession s = req.getSession();
		if (!((User) s.getAttribute("user")).getRole().equals("professor")) {
			res.sendRedirect(loginpath);
			return;
		}
	
		chain.doFilter(request, response);
	}

	
	public void init(FilterConfig fConfig) throws ServletException {}
}