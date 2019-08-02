package ch.blobber.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.blobber.database.AuthDatabase;
import ch.blobber.database.WalletDatabase;
import ch.blobber.wallet.DogecoinConnection;

public class SendMyself {
	@WebServlet("/sendMyself")
	public class SendAddressServlet extends HttpServlet {

		public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
			String code = req.getParameter("code");
			String token = req.getParameter("token");
			
			PrintWriter out = res.getWriter();
			
			if (code == null || token == null) {
				out.print(ServletErrors.INTERNAL_ERROR.toJson());
				return;
			}
			
			int user;
			AuthDatabase db = new AuthDatabase();
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
			
			out.print(claimURL(code, user));
					
		}

		
		private String claimURL(String urlCode, int user) {
			// implement Tax (-1 Dogecoin)
			int current_tax = 1;
			WalletDatabase db = new WalletDatabase();
			float balance = 0;
			try {
				balance = db.getURLBalance(urlCode)-current_tax;
				db.lootURL(urlCode, String.valueOf(user));
			} catch (SQLException e) {
				e.printStackTrace();
				return ServletErrors.INTERNAL_ERROR.toJson();
			}
			DogecoinConnection c = new DogecoinConnection();
			try {
				c.move(0, user, balance);
			} catch (Exception e) {
				e.printStackTrace();
				return ServletErrors.INTERNAL_ERROR.toJson();
			}
			return ServletErrors.NO_ERROR.toJson();
		}
	}
}
