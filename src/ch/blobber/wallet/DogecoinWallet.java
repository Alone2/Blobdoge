package ch.blobber.wallet;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import ch.blobber.database.WalletDatabase;

public class DogecoinWallet {
	DogecoinConnection c;
	
	public DogecoinWallet() {
		c = new DogecoinConnection("http://127.0.0.1:8332/", "alain", "verysecure");
	}

	private String sendToAddress(int account, String address, float amount) {
		try {
			if (!c.validateAddress(address)) 
				return "{\"error\":\"invalid_address\"}";
			c.sendFromAccount(account, address, amount);
			return "{\"error\":\"none\"}";
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"error\":\"internal_error\"}";
		}
	}
	
	public String claimURL(String urlCode, String address) {
		// implement Tax (-1 Dogecoin)
		int current_tax = 1;
		WalletDatabase db = new WalletDatabase();
		float balance = 0;
		try {
			balance = db.getURLBalance(urlCode)-1;
			db.lootURL(urlCode, address);
		} catch (SQLException e) {
			e.printStackTrace();
			return "{\"error\":\"internal_error\"}";
		}
		return sendToAddress(0, address, balance);
		
	}
	
	public String sendToURL(int account, float amount) {
		String url;
		try {
			if (!(c.getBalance(account) >= amount))
				return "{\"error\":\"not_enough_money\"}";
			c.move(account, 0, amount);
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"error\":\"internal_error\"}";
		}
		WalletDatabase db = new WalletDatabase();
		try {
			url = db.createURL(account, amount);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return "{\"error\":\"internal_error\"}";
		}
		
		
		JSONObject j = new JSONObject();
		j.put("error", "none");
		j.put("code", url);
		j.put("balance", amount);
		return j.toString();
	}


	public String getInfo(int account) {
		String address;
		float balance;
		try {
			address = c.getCurrentAddress(account);
			balance = c.getBalance(account);
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"error\":\"internal_error\"}";
		}
		JSONObject j = new JSONObject();
		j.put("error", "none");
		j.put("address", address);
		j.put("balance", balance);
		return j.toString();
	}


}
