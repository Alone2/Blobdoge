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
import ch.blobber.wallet.DogecoinConnection;

@WebServlet("/sendURLAddress")
public class SendURLAddressServlet extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String token = req.getParameter("token");
		String amount = req.getParameter("amount");
		String address = req.getParameter("address");
		
		PrintWriter out = res.getWriter();
		if (token == null || amount == null || address == null) {
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
		
		out.print(this.sendToURL(user, Float.valueOf(amount), address));
		
	}
	
	private String sendToURL(int account, float amount, String address) {
		DogecoinConnection c = new DogecoinConnection();
		try {
			if (!(c.getBalance(account) >= amount && amount > 0))
				return ServletErrors.NOT_ENOUGH_MONEY.toJson();
		    if(!c.validateAddress(address))
		    	return ServletErrors.INVALID_ADDRESS.toJson();
		    try {
		    	int accReceiver = c.getAccount(address);
		    	if (accReceiver != 0) {
		    		c.move(account, accReceiver, amount);
		    	} else {
		    		throw new Exception();
		    	}
		    } catch (Exception e) {
		    	c.sendFromAccount(account, address, amount);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}
		
		JSONObject j = new JSONObject();
		j.put("error", "none");
		j.put("address", address);
		j.put("balance", amount);
		return j.toString();
	}
}
