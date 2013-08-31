package executables;

import interpreter.Returnable;
import types.TypedValue;

public class Factorial extends Returnable{
private TypedValue left;
	
	public Factorial(TypedValue left) {
		this.left = left;
	}
	
	public TypedValue execute() throws Exception {
		return left.factorial();
	}
}
