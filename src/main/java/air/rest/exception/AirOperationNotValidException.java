package air.rest.exception;

public class AirOperationNotValidException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public AirOperationNotValidException(String message) {
        super(message);
    }
}
