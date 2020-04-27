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
import ch.blobber.database.PaymentDatabase;
import ch.blobber.database.WalletDatabase;
import ch.blobber.wallet.DogecoinConnection;

@WebServlet("/devInfoAddress")
public class DevInfoAddressServlet extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String token = req.getParameter("devtoken");
		String address = req.getParameter("address");

		PrintWriter out = res.getWriter();
		if (token == null || address == null) {
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

		out.print(this.getInfo(user, address));
	}

	private String getInfo(int account, String address) {
		DogecoinConnection c = new DogecoinConnection();

		try {
			if (!c.validateAddress(address) || c.getAccount(address) != account)
				return ServletErrors.INVALID_ADDRESS.toJson();
		} catch (Exception e1) {
			e1.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}

		float balance;
		try {
			balance = c.getReceivedByAddress(address, 1);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}

		float balanceUnconfirmed;
		try {
			balanceUnconfirmed = c.getReceivedByAddress(address, 0);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}

		float balanceBlobber = 0;
		try {
			int accReceiver = c.getAccount(address);
			if (accReceiver != 0) {
				PaymentDatabase p = new PaymentDatabase();
				balanceBlobber = p.getPaymentBalance(address);
				p.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServletErrors.INTERNAL_ERROR.toJson();
		}
		
		JSONObject j = new JSONObject();
		j.put("error", "none");
		j.put("balance", balance + balanceBlobber);
		j.put("balanceUnconfirmed", balanceUnconfirmed);
		return j.toString();
	}
}
