package ch.blobber.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

public class WalletDatabase extends Database {

	/*
	 * CREATE TABLE walletTable( id INT AUTO_INCREMENT, originUserId INT, balance
	 * BIGINT, url VARCHAR(255), isDone BOOL,  claimedBy VARCHAR(100), PRIMARY KEY(id));
	 */

	public String createURL(int userId, float amount) throws SQLException {
		String url = generateURL(20);
		
		String sql = "INSERT INTO walletTable (originUserId, balance, url, isDone) VALUES (?,?,?,?);";
		PreparedStatement st;
		st = con.prepareStatement(sql);
		st.setInt(1, userId);
		st.setFloat(2, amount);
		st.setString(3, url);
		st.setInt(4, 0);
		st.execute();
		st.close();
		
		return url;
	}

	public float getURLBalance(String url) throws SQLException {
		String request = "SELECT balance FROM walletTable WHERE url=? AND isDone = 0;";
		float balance = 0;
		PreparedStatement st;
		st = con.prepareStatement(request);
		st.setString(1, url);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			balance = rs.getFloat("balance");
		}
		rs.close();
		return balance;
	}

	public void lootURL(String url, String address) throws SQLException {
		String sql = "UPDATE walletTable SET isDone = ?, balance = ?, claimedBy = ? WHERE url = ? ;";
		PreparedStatement st;
		st = con.prepareStatement(sql);
		st.setFloat(1, 1);
		st.setFloat(2, 0);
		st.setString(3, address);
		st.setString(4, url);
		st.execute();
		st.close();
	}
	
	public JSONArray getCreatedByUser(int userId) throws Exception {
		String request = "SELECT url FROM walletTable WHERE originUserId=? AND isDone = 0;";
		JSONArray output = new JSONArray();
		
		PreparedStatement st;
		st = con.prepareStatement(request);
		st.setInt(1, userId);
		
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			output.put(rs.getString("url"));
		}
		rs.close();
		return output;
	}

	private String generateURL(int size) throws SQLException {
		// Generate a token
		String output = randomLetters(size);
		// Test if token already exists
		String sql = "SELECT COUNT(url) FROM walletTable WHERE url= ? ;";
		PreparedStatement st;
		st = con.prepareStatement(sql);
		st.setString(1, output);
		ResultSet rs = st.executeQuery();
		int i = 0;
		while (rs.next()) {
			i = rs.getInt(1);
		}
		rs.close();
		
		if (i > 0)
			return generateURL(size);
		return output;
	}
}
