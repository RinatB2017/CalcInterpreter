package inter.returnables;

import inter.Returnable;
import types.TypedValue;

public class Factorial extends Returnable{
	
	public Factorial(TypedValue left) throws Exception {
		super(left, null);
	}
	
	public TypedValue execute() throws Exception {
		return left.factorial();
	}
}
