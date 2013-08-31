package executables;

import interpreter.Returnable;
import types.TypedValue;

public class Degree extends Returnable {
	private TypedValue left, degree;
	
	public Degree(TypedValue left, TypedValue degree) {
		this.left = left;
		this.degree = degree;
	}
	
	public TypedValue execute() throws Exception {
		return left.degree(degree);
	}
	
}
