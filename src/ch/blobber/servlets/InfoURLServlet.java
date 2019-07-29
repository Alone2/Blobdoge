package ch.blobber.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import ch.blobber.database.WalletDatabase;

@WebServlet("/infoURL")
public class InfoURLServlet extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String url = req.getParameter("code");
		
		PrintWriter out = res.getWriter();
		if (url == null) {
			out.print(ServletErrors.PARAMETER_ERROR.toJson());
			return;
		}
		
		WalletDatabase w = new WalletDatabase();
		float balance = 0;
		try {
			balance = w.getURLBalance(url);				
		} catch (SQLException e) {
			e.printStackTrace();
			out.print(ServletErrors.INTERNAL_ERROR.toJson());
			return;
		}
		if (balance <= 0) {
			out.print(ServletErrors.INVALID_CODE.toJson());
			return;
		}
		JSONObject j = new JSONObject();
		j.put("error", "none");
		j.put("balance", balance);
		out.print(j.toString());
		
	}
}
