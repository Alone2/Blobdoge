package ch.blobber.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

public class PaymentDatabase extends Database {

	/*
	 * CREATE TABLE paymentTable( id INT AUTO_INCREMENT, balance
	 * BIGINT, address VARCHAR(100), PRIMARY KEY(id));
	 */

	public boolean createPaymentRequest(String address) throws SQLException {	
		// test if address doesn't exist already
		if (doesExist(address)) 
			return false;
			
		// create payment request
		String sql = "INSERT INTO paymentTable (address,balance) VALUES (?,?);";
		PreparedStatement st;
		st = con.prepareStatement(sql);
		st.setString(1, address);
		st.setFloat(2, 0);
		st.execute();
		st.close();
		
		return true;
	}
	

	public float getPaymentBalance(String address) throws SQLException {
		String request = "SELECT balance FROM paymentTable WHERE address = ?;";
		float balance = 0;
		PreparedStatement st;
		st = con.prepareStatement(request);
		st.setString(1, address);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			balance = rs.getFloat("balance");
		}
		rs.close();
		return balance;
	}
	
	
	public boolean setPaymentBalance(String address, float balance) throws SQLException {
		if (!doesExist(address)) 
			createPaymentRequest(address);
		String sql = "UPDATE paymentTable SET balance = ? WHERE address = ?;";
		PreparedStatement st;
		st = con.prepareStatement(sql);
		st.setFloat(1, balance);
		st.setString(2, address);
		st.execute();
		st.close();
		return true;
	}
	
	private boolean doesExist(String address) throws SQLException {
		String request = "SELECT COUNT(*) FROM paymentTable WHERE address=?;";
		float count = 0;
		PreparedStatement st;
		st = con.prepareStatement(request);
		st.setString(1, address);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			count = rs.getFloat(1);
		}
		rs.close();
		if (count > 0) 
			return true;
		return false;

		
	}

}
