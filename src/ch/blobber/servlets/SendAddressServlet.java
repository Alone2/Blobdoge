package ch.blobber.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.blobber.database.WalletDatabase;
import ch.blobber.wallet.DogecoinConnection;

@WebServlet("/sendAddress")
public class SendAddressServlet extends HttpServlet {
	
	private DogecoinConnection c ;
	
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String code = req.getParameter("code");
		String address = req.getParameter("address");
		
		PrintWriter out = res.getWriter();
		if (code == null || address == null) {
			out.print(ServletErrors.INTERNAL_ERROR.toJson());
			return;
		}
		
		this.c = new DogecoinConnection();
		try {
			if (!c.validateAddress(address)) {
				out.print(ServletErrors.INVALID_ADDRESS.toJson());
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(ServletErrors.INTERNAL_ERROR.toJson());
			return;
		}		
		out.print(this.claimURL(code, address));		
	}
	
	private String sendToAddress(int account, String address, float amount) {
		try {
			c.sendFromAccount(account, address, amount);
			return ServletErrors.NO_ERROR.toJson();
		} catch (Exception e) {
			e.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}
	}
	
	private String claimURL(String urlCode, String address) {
		// implement Tax (-1 Dogecoin)
		int current_tax = 1;
		WalletDatabase db = new WalletDatabase();
		float balance = 0;
		try {
			balance = db.getURLBalance(urlCode)-current_tax;
			db.lootURL(urlCode, address);
		} catch (SQLException e) {
			e.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}
		return sendToAddress(0, address, balance);
	}
}