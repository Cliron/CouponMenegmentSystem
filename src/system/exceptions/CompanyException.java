package system.exceptions;

public class CompanyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompanyException(){
		super();
	}

	public CompanyException(String message) {
		super(message);
		
	}
	
}
