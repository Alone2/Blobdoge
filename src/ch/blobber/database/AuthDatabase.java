package ch.blobber.database;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AuthDatabase {
	final String url = "jdbc:mysql://localhost:3306/blob_test";
	final String uname = "blobber";
	final String passwd = "test123";
	Connection con;

	public AuthDatabase() {
		// Initalize connection
		try {
			Class.forName("org.mariadb.jdbc.Driver");
		} 
		catch (ClassNotFoundException e) {
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
	
	public int getUserId(String token) {
		// search userId with specific Token
		String request = "SELECT id "
				+ "FROM authTable WHERE "
				+ "uniqueId='" + token + "';";
		int id = 0;
		try {
			Statement st = con.createStatement(); 
			ResultSet rs = st.executeQuery(request);
			while (rs.next()) {
				  id = rs.getInt("id");
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return id;
		
	}

	public String logIn(String username, String password) {
		// Get salt and password from Database
		String request = "SELECT salt,password "
				+ "FROM authTable WHERE "
				+ "username='" + username + "';";
		byte[] storedPassword = new byte[256];
		byte[] salt = new byte[16];
		try {
			Statement st = con.createStatement(); 
			ResultSet rs = st.executeQuery(request);
			while (rs.next()) {
				  storedPassword = rs.getBytes("password");
				  salt = rs.getBytes("salt");
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"error\":\"internal_error\"}";
		}
		
		// Encrypt Password
		byte[] encryptedPassword;
		try {
			encryptedPassword = encryptPassword(password, salt);
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"error\":\"internal_error\"}";
		}
		
		// Compare stored and encrypted (typed) password
		if (!Arrays.equals(encryptedPassword, storedPassword)) {
			return "{\"error\":\"wrong_password\"}";
		}
		
		// Generate token for User
		String token = generateToken(40);
		
		String sql = "UPDATE authTable SET "
				+ "uniqueId = ? "
				+ "WHERE username='" + username + "';";
		PreparedStatement st;
		try {
			st = con.prepareStatement(sql);
			st.setString(1, token);
			st.execute();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return "{\"error\":\"internal_error\"}";
		}
		
		return "{\"error\":\"none\",\"token\":\"" + token + "\"}";
		
	}

	public String register(String username, String password) {
		System.out.println(username + " registers");
		// Test if username exists
		int i = this.getInt("SELECT COUNT(username)\n" 
				+ "FROM authTable " 
				+ "WHERE username='" + username + "';");
		if (i > 0) {
			return "{\"error\":\"username_taken\"}";
		}
		
		//encrypt Password
		byte[] salt = getSalt();
		byte[] encryptedPassword;
		try {
			encryptedPassword = encryptPassword(password, salt);
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"error\":\"internal_error\"}";
		}
		
		// Upload to Database
		String sql = "INSERT INTO authTable (" 
				+ "username, password, salt" 
				+ ") VALUES (?,?,?);";
		PreparedStatement st;
		try {
			st = con.prepareStatement(sql);
			st.setString(1, username);
			st.setBytes(2, encryptedPassword);
			st.setBytes(3, salt);
			st.execute();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return "{\"error\":\"none\"}";
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
	
	private static byte[] getSalt() {
		// generate salt
		final Random r = new SecureRandom();
		byte[] salt = new byte[16];
		r.nextBytes(salt);
		return salt;
	}
	
	private byte[] encryptPassword(String pass, byte[] salt) throws Exception {
		int keyLenght = 32;
		// https://stackoverflow.com/questions/22580853/reliable-implementation-of-pbkdf2-hmac-sha256-for-java/27928435#27928435
		KeySpec spec = new PBEKeySpec(
                pass.toCharArray(),
                salt,
                4096,
                keyLenght * 8
                );
		
		SecretKeyFactory f;
		byte[] encoded;
		f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		encoded = f.generateSecret(spec).getEncoded();
		
		//String str = new String(encoded, StandardCharsets.UTF_8);
		//System.out.println("Encoded: " + str);

		return encoded;	
	}
	
	private String generateToken(int size) {
		// Generate a token
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0987654321";
		String output = new String();
		final Random rand = new SecureRandom();
		for (int i = 0; i < size; i++) {
			int randInt = rand.nextInt(characters.length());
			output += characters.charAt(randInt);
		}
		// Test if token already exists
		int i = this.getInt("SELECT COUNT(uniqueId)\n" 
				+ "FROM authTable " 
				+ "WHERE uniqueId='" + output + "';");
		if (i > 0) 
			return generateToken(size);
		return output;
	}
	
	private int getInt(String request) {
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
