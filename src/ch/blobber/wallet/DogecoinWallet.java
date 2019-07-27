package ch.blobber.wallet;

public class DogecoinWallet {
	public DogecoinWallet() {
		
	}
	
	public boolean sendToAdress(int account, String address) {
		return false;
	}
	// I don't even need this function because, what should I do
	// after a payment is recieved?? Exactly - nothing.
	// The User can just get his Balance.
	// When the user gets his Adress and someone sent him something
	// the Adress changes. Fuck, I still need this function (check getAdress())
	// maybe private?
	public int paymentRecieved(String adress) {
		// returns account if recieved
		return 0;
	}
	
	public String getAdress(int account) {
		// if payment recieved -> return newAdress
		return "";
	}
	
	public String newAdress(int account) {
		return "";
	}
	
	public float getBalance(int account) {
		return 0;
	}
	
}
