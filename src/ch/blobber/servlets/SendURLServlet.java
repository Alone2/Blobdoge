package ch.blobber.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import ch.blobber.database.AuthDatabase;
import ch.blobber.database.WalletDatabase;
import ch.blobber.wallet.DogecoinConnection;

@WebServlet("/sendURL")
public class SendURLServlet extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String token = req.getParameter("token");
		String amount = req.getParameter("amount");
		
		PrintWriter out = res.getWriter();
		if (token == null || amount == null) {
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
		
		out.print(this.sendToURL(user, Float.valueOf(amount)));
		
	}
	
	private String sendToURL(int account, float amount) {
		DogecoinConnection c = new DogecoinConnection();
		String url;
		try {
			if (!(c.getBalance(account) >= amount && amount > 0))
				return ServletErrors.NOT_ENOUGH_MONEY.toJson();
			c.move(account, 0, amount);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}
		WalletDatabase db = new WalletDatabase();
		try {
			url = db.createURL(account, amount);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}
		
		JSONObject j = new JSONObject();
		j.put("error", "none");
		j.put("code", url);
		j.put("balance", amount);
		return j.toString();
	}
}
