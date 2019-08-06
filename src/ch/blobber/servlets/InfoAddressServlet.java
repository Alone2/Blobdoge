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

@WebServlet("/infoAddress")
public class InfoAddressServlet extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String token = req.getParameter("token");
		
		PrintWriter out = res.getWriter();
		if (token == null) {
			out.print(ServletErrors.PARAMETER_ERROR.toJson());
			return;
		}
		
		AuthDatabase db = new AuthDatabase();
		int user;
		try {
			user = db.getUserId(token);
			db.close();
			if (user == 0) {
				 out.print(ServletErrors.WRONG_KEY.toJson());
				 return;
			}
		} catch (SQLException e) {
			 out.print(ServletErrors.INTERNAL_ERROR.toJson());
			 return;
		}
		
		out.print(this.getInfo(user));
	}
	
	private String getInfo(int account) {
		DogecoinConnection c = new DogecoinConnection();
		String address;
		float balance;
		try {
			address = c.getCurrentAddress(account);
			balance = c.getBalance(account);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}
		
		// Get all unclaimed Links
		JSONArray codes = new JSONArray();
		WalletDatabase dw = new WalletDatabase();
		try {
			codes = dw.getCreatedByUser(account);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}
		
		JSONObject j = new JSONObject();
		j.put("error", "none");
		j.put("address", address);
		j.put("balance", balance);
		j.put("codes", codes);
		return j.toString();
	}
}
