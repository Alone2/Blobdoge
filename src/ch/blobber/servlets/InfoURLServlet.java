package ch.blobber.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.blobber.database.WalletDatabase;

@WebServlet("/infoURL")
public class InfoURLServlet extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String url = req.getParameter("code");
		PrintWriter out = res.getWriter();
		
		WalletDatabase w = new WalletDatabase();
		float balance = 0;
		try {
			balance = w.getURLBalance(url);				
		} catch (SQLException e) {
			e.printStackTrace();
			out.print("{\"error\":\"internal_error\"}");
			return;
		}
		if (balance <= 0) {
			out.print("{\"error\":\"invalid_code\"}");
			return;
		}
		out.print("{\"error\":\"none\",\"balance\":" + String.valueOf(balance) +"}");
		
	}
}
