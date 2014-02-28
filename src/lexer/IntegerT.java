package lexer;

import types.*;

public class IntegerT extends Token{
	final public int value;
	
	public IntegerT(Tag n, int value) {
		super(n);
		this.value=value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public void sendTypedValueTo(TypedValue o) throws Exception{
		o.setInt(value);
	}
}
