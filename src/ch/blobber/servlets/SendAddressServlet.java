package ch.blobber.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.blobber.wallet.DogecoinWallet;

@WebServlet("/sendAddress")
public class SendAddressServlet extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String code = req.getParameter("code");
		String address = req.getParameter("address");
		
		PrintWriter out = res.getWriter();
		
		DogecoinWallet dw = new DogecoinWallet();
		out.print(dw.claimURL(code, address));
		
	}
}