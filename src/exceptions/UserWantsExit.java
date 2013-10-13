package exceptions;

public class UserWantsExit extends Exception {
	private static final long serialVersionUID = 1L;
	private int exitcode;
	
	public UserWantsExit(int exitcode){
		this.exitcode=exitcode;
	}

	public int getExitcode() {
		return exitcode;
	}
}
