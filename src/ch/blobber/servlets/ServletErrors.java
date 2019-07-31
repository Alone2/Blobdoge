package ch.blobber.servlets;

public enum ServletErrors {
	  INTERNAL_ERROR("{\"error\":\"internal_error\"}"),
	  PARAMETER_ERROR( "{\"error\":\"parameter_error\"}"),
	  NOT_ENOUGH_MONEY("{\"error\":\"not_enough_money\"}"),
	  INVALID_PASSWORD(""),
	  INVALID_ADDRESS("{\"error\":\"invalid_address\"}"),
	  INVALID_CODE("{\"error\":\"invalid_address\"}"),
	  WRONG_KEY("{\"error\":\"wrong_key\"}"),
	  NO_ERROR("{\"error\":\"none\"}"),
	  PASSWORD_OR_USERNAME_EMPTY("{\"error\":\"password_or_username_empty\"}");

	  private final String json;

	  private ServletErrors(String json) {
	    this.json = json;
	  }

	  public String toJson() {
	     return json;
	  }

}
