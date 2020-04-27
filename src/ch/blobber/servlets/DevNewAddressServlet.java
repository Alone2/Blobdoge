package ch.blobber.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import ch.blobber.database.AuthDatabase;
import ch.blobber.database.WalletDatabase;
import ch.blobber.wallet.DogecoinConnection;

@WebServlet("/devNewAddress")
public class DevNewAddressServlet extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String token = req.getParameter("devtoken");
		
		PrintWriter out = res.getWriter();
		if (token == null) {
			out.print(ServletErrors.PARAMETER_ERROR.toJson());
			return;
		}
		
		// Check if dev token is valid
		AuthDatabase db = new AuthDatabase();
		int user;
		try {
			user = db.getUserIdFromDevToken(token);
			db.close();
			if (user == 0) {
				 out.print(ServletErrors.WRONG_KEY.toJson());
				 return;
			}
		} catch (SQLException e) {
			 out.print(ServletErrors.INTERNAL_ERROR.toJson());
			 return;
		}
		
		// generate new address and output it
		DogecoinConnection c = new DogecoinConnection();
		String newAddress;
		try {
			newAddress = c.getNewAddress(user);
		} catch (Exception e) {
			out.print(ServletErrors.INTERNAL_ERROR.toJson());
			return;
		}		
		JSONObject j = new JSONObject();
		j.put("error", "none");
		j.put("address", newAddress);
		out.print(j.toString());
		
	}
	
}
