package ecommerce_order_api.services.exceptions;

public class DataBaseException extends RuntimeException {

	
	private static final long serialVersionUID = 1L;
	
	public DataBaseException(String msg) {
		super(msg);
	}

}
