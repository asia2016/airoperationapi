package air.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "AirOperation not provided")
public class AirOperationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
