package inter.returnables;

import inter.Returnable;
import types.TypedValue;

public class Degree extends Returnable {
	public Degree(TypedValue left, TypedValue degree) throws Exception {
		super(left, degree);
	}
	
	public TypedValue execute() throws Exception {
		return left.degree(right);
	}
	
}
