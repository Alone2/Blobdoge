package ch.blobber.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import ch.blobber.wallet.DogecoinConnection;

@SuppressWarnings("serial")
public class Startup extends HttpServlet {

	public void init() throws ServletException {
		// Thread t = new Thread();
		// Test if Server ok
		DogecoinConnection c = new DogecoinConnection("http://127.0.0.1:8332/", "alain", "verysecure");

		boolean isReady;
		boolean error;

		try {
			isReady = c.isReady();
			error = false;
		} catch (Exception e) {
			isReady = false;
			error = true;
			e.printStackTrace();
		}

		if (error) {
			System.out.println("-----------------------------------------------------");
			System.out.println("--------Blobcoin Error - Can't access Wallet---------");
			System.out.println("-----------------------------------------------------");
			return;
		} 
		if (!isReady) {
			System.out.println("---------------------------------------------");
			System.out.println("---BlobDoge Error - Wallet is not in sync!---");
			System.out.println("---(Blockchain is not fully downloaded)------");
			System.out.println("---------------------------------------------");
		}

	}
}