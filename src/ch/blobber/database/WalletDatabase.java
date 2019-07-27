package ch.blobber.database;

import java.sql.Connection;

public class WalletDatabase {
	final String url = "jdbc:mysql://localhost:3306/blob_test";
	final String uname = "blobber";
	final String passwd = "test123";
	Connection con;

	public WalletDatabase() {
		
	}
	
	public void newAddress(String address, int userId) {
		
	}
	
	public void disableAdress(String address) {
		
	}
}
