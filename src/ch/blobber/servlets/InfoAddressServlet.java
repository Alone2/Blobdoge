package ch.blobber.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.blobber.database.AuthDatabase;
import ch.blobber.wallet.DogecoinWallet;

@WebServlet("/infoAddress")
public class InfoAddressServlet extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String token = req.getParameter("token");
		PrintWriter out = res.getWriter();
		
		AuthDatabase db = new AuthDatabase();
		int user;
		try {
			user = db.getUserId(token);
			db.close();
			if (user == 0) {
				 out.print("{\"error\":\"wrong_key\"}");
				 return;
			}
		} catch (SQLException e) {
			 out.print("{\"error\":\"internal_error\"}");
			 return;
		}
		
		DogecoinWallet d = new DogecoinWallet();		
		out.print(d.getInfo(user));
	}
}
