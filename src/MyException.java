
public class MyException extends Exception{
	private static final long serialVersionUID = 1L;
	String message;
	MyException(String message){
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
