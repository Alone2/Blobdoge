package ch.blobber.wallet;


import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import ch.blobber.database.PropertiesCon;

import java.io.*;


public class DogecoinConnection {
	String output;
	String web_url;
	String uname;
	String pass;
	
	public DogecoinConnection() {
		PropertiesCon prop = new PropertiesCon();
		web_url = prop.getParameter("dogecoind.url");
		uname = prop.getParameter("dogecoind.uname");
		pass = prop.getParameter("dogecoind.passwd");
	}
	

	private String get(String method, String args) throws Exception {
		
		URL url = new URL(web_url);

		String credentials = uname + ":" + pass;
		String encoding = Base64.getEncoder().encodeToString((credentials).getBytes("UTF-8"));

		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) con;

		http.setRequestMethod("POST");
		http.setRequestProperty("Authorization", "Basic " + encoding);
		http.setDoOutput(true);

		String outStr = "{\"jsonrpc\":\"1.0\",\"id\":\"myJavaClient\",\"method\":\"" + method + "\",\"params\":" + args + "}";
		byte[] out = outStr.getBytes(StandardCharsets.UTF_8);

		http.setRequestProperty("content-type", "text/plain;");

		http.connect();
		try (OutputStream os = http.getOutputStream()) {
			os.write(out);
		}
		StringBuilder content;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

			String line;
			content = new StringBuilder();

			while ((line = in.readLine()) != null) {
				content.append(line);
				content.append(System.lineSeparator());
			}
			this.output = content.toString();
		}

		return this.output;
	}
	
	public JSONObject send(String command, JSONArray args) throws Exception {
		String output = this.get(command, args.toString());
		return new JSONObject(output);
	}
		
	public boolean isReady() throws Exception {
		JSONArray arr = new JSONArray();
		JSONObject out;
		out = this.send("getblockchaininfo", arr);
		JSONObject result = out.getJSONObject("result");
		if (result.getInt("blocks") < result.getInt("headers")) {
			return false;
		}
		return true;
	}
	
	public float getBalance(int account) throws Exception {
		return getBalance(account, 1);
	}
	
	public float getBalance(int account, int minconf) throws Exception {
		JSONArray arr = new JSONArray();
		arr.put(String.valueOf(account));
		arr.put(minconf);
		JSONObject out = this.send("getbalance", arr);

		return out.getFloat("result");
	}
	
	public String sendFromAccount(int account, String dogeAddress, float amount) throws Exception {
		JSONArray arr = new JSONArray();
		arr.put(String.valueOf(account));
		arr.put(dogeAddress);
		arr.put(amount);
		System.out.println(arr.toString());
		JSONObject out = this.send("sendfrom", arr);
		//result will show transactionId if successful
		//return true;
		return out.getString("result");
	}
	
	public boolean move(int account, int account2, float amount) throws Exception {
		JSONArray arr = new JSONArray();
		arr.put(String.valueOf(account));
		arr.put(String.valueOf(account2));
		arr.put(amount);
		JSONObject out = this.send("move", arr);
		//result output true/false
		//return true;
		return out.getBoolean("result");
	}
	
	public String getCurrentAddress(int account) throws Exception {
		//creates Account if none or transaction recieved.
		JSONArray arr = new JSONArray();
		arr.put(String.valueOf(account));
		JSONObject out = this.send("getaccountaddress", arr);
		return out.getString("result");
	}
	
	public String getNewAddress(int account) throws Exception {
		// creates new address for account
		JSONArray arr = new JSONArray();
		arr.put(String.valueOf(account));
		JSONObject out = this.send("getnewaddress", arr);
		
		return out.getString("result");
	}
	
	public float getReceivedByAddress(String address) throws Exception {
		return getReceivedByAddress(address, 1);
	}
	
	public float getReceivedByAddress(String address, int minconf) throws Exception {
		JSONArray arr = new JSONArray();
		arr.put(address);
		arr.put(minconf);
		JSONObject out = this.send("getreceivedbyaddress", arr);
		return out.getFloat("result");
	}
	
	public boolean validateAddress(String address) throws Exception {
		JSONArray arr = new JSONArray();
		arr.put(address);
		JSONObject out = this.send("validateaddress", arr);
		JSONObject result = out.getJSONObject("result");
		
		return result.getBoolean("isvalid");
	}
	
	public int getAccount(String address) throws Exception {
		JSONArray arr = new JSONArray();
		arr.put(address);
		JSONObject out =  this.send("getaccount", arr);
		int o = Integer.valueOf(out.getString("result"));
		
		return o;
	}
	
	
}