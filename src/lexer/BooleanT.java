package lexer;

import interpretator.TypedValue;



public class BooleanT extends Token{
	final public boolean value;
	
	public BooleanT(Tag n, boolean value) {
		super(n);
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "" + value;
	}
	
	//@Override
	public static TypedValue createTypedValue(){
		return null;
	}
}
