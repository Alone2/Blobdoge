package ch.blobber.database;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Database {
	final String url = "jdbc:mysql://localhost:3306/BlobDoge";
	final String uname = "blobber";
	final String passwd = "test123";
	Connection con;
	
	public Database() {
		// Initalize connection
		try {
			Class.forName("org.mariadb.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			con = DriverManager.getConnection(url, uname, passwd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		// close everything
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected String randomLetters(int size) {
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0987654321";
		String output = new String();
		final Random rand = new SecureRandom();
		for (int i = 0; i < size; i++) {
			int randInt = rand.nextInt(characters.length());
			output += characters.charAt(randInt);
		}
		return output;
	}
	
	// ONLY USE WHEN NO USER INPUT!!!!! (SQL Injection)
	protected int getInt(String request) {
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(request);
			if (rs.next()) {
				return rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}
	
}
