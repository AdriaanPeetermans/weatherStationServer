package exceptions;

public class BasisException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BasisException(String reason) {
		this.reason = reason;
	}
	
	public void printStackTrace() {
		super.printStackTrace();
		System.out.println("REASON: ".concat(this.reason));
	}
	
	public final String reason;

}
