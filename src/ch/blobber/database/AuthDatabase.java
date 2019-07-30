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

public class AuthDatabase extends Database {

	public int getUserId(String token) throws SQLException {
		// search userId with specific Token
		String request = "SELECT id FROM authTable WHERE uniqueId= ? ;";
		int id = 0;
		PreparedStatement st = con.prepareStatement(request);
		st.setString(1, token);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			id = rs.getInt("id");
		}
		rs.close();
		return id;

	}

	public String logIn(String username, String password) throws Exception {
		// Get salt and password from Database
		String request = "SELECT salt,password FROM authTable WHERE username=?;";
		byte[] storedPassword = new byte[256];
		byte[] salt = new byte[16];
		PreparedStatement st = con.prepareStatement(request);
		st.setString(1, username);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			storedPassword = rs.getBytes("password");
			salt = rs.getBytes("salt");
		}
		rs.close();

		// Encrypt Password
		byte[] encryptedPassword;
		encryptedPassword = encryptPassword(password, salt);

		// Compare stored and encrypted (typed) password
		if (!Arrays.equals(encryptedPassword, storedPassword)) {
			return "{\"error\":\"wrong_password\"}";
		}

		// Generate token for User
		String token = generateToken(40);

		String sql = "UPDATE authTable SET uniqueId = ? WHERE username= ? ;";
		PreparedStatement st2;
		st2 = con.prepareStatement(sql);
		st2.setString(1, token);
		st2.setString(2, username);
		st2.execute();
		st2.close();

		return "{\"error\":\"none\",\"token\":\"" + token + "\"}";

	}

	public String register(String username, String password) throws Exception {
		System.out.println(username + " registers");
		
		if (doesUserExist(username)) {
			return "{\"error\":\"username_taken\"}";
		}

		// encrypt Password
		byte[] salt = getSalt();
		byte[] encryptedPassword;
		encryptedPassword = encryptPassword(password, salt);

		// Upload to Database
		String sql = "INSERT INTO authTable (username, password, salt) VALUES (?,?,?);";
		PreparedStatement st;
		st = con.prepareStatement(sql);
		st.setString(1, username);
		st.setBytes(2, encryptedPassword);
		st.setBytes(3, salt);
		st.execute();
		st.close();

		return "{\"error\":\"none\"}";
	}
	
	private boolean doesUserExist(String username) throws SQLException {
		String sql = "SELECT COUNT(username) FROM authTable WHERE username = ? ;";
		PreparedStatement st;
		st = con.prepareStatement(sql);
		st.setString(1, url);
		ResultSet rs = st.executeQuery();
		int timesHere = 0;
		while (rs.next()) {
			timesHere = rs.getInt(1);
		}
		rs.close();
		if (timesHere > 0)
			return true;
		return false;
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
		KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 4096, keyLenght * 8);

		SecretKeyFactory f;
		byte[] encoded;
		f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		encoded = f.generateSecret(spec).getEncoded();

		// String str = new String(encoded, StandardCharsets.UTF_8);
		// System.out.println("Encoded: " + str);

		return encoded;
	}

	private String generateToken(int size) throws SQLException {
		// Generate a token
		String output = randomLetters(size);
		// Test if token already exists
		String sql ="SELECT COUNT(uniqueId) FROM authTable WHERE uniqueId = ? ;";
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
			return generateToken(size);
		return output;
	}
	

}
