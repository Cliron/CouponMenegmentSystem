package system.exceptions;

public class CustomerException extends RuntimeException {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomerException(){
		super();
	}

	public CustomerException(String message) {
		super(message);
		
	}
}