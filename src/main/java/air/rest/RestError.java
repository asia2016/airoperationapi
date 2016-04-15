package air.rest;

public class RestError {

	int code;
	String message;
	
	public RestError(){}

	public RestError(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
