package ch.blobber.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.blobber.database.AuthDatabase;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String register = req.getParameter("register");
		String uname = req.getParameter("uname");
		String passwd = req.getParameter("passwd");

		PrintWriter out = res.getWriter();
		if (register == null || uname == null || passwd == null) {
			out.print(ServletErrors.PARAMETER_ERROR.toJson());
			return;
		}
		
		if (register.contains("True") || register.contains("true")) {
			AuthDatabase a = new AuthDatabase();
			try {
				out.print(a.register(uname, passwd));
			} catch (Exception e) {
				e.printStackTrace();
				out.print(ServletErrors.INTERNAL_ERROR.toJson());
				return;
			}
			a.close();
			return;
		}
		AuthDatabase a = new AuthDatabase();
		try {
			out.print(a.logIn(uname, passwd));
		} catch (Exception e) {
			e.printStackTrace();
			out.print(ServletErrors.INTERNAL_ERROR.toJson());
			return;
		}		
		a.close();
	}
	
	
}