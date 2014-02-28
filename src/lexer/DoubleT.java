package lexer;

import types.*;

public class DoubleT extends Token{
	final public double value;
	
	public DoubleT(Tag name, double value) {
		super(name);
		this.value=value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public void sendTypedValueTo(TypedValue o) throws Exception{
		o.setDouble(value);
	}
}
