package system.exceptions;

public class CouponException extends RuntimeException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CouponException(){
		super();
	}
	public CouponException(String message){
		super(message);
	}
}
