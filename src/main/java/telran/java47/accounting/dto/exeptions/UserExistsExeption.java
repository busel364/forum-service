package telran.java47.accounting.dto.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserExistsExeption extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7221596901660152401L;

}
