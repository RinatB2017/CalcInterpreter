package interpretator;

import types.TypedValue;

public class TableGet implements Returnable {

	private Interpreter i; 
	private String name;
	
	public TableGet(String name, Interpreter i) {
		this.i=i;
		this.name=name;
	}

	@Override
	public TypedValue execute() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
