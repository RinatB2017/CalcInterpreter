package lexer;

import types.TypedValue;
import types.Types;



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
	
	public void getTypedValueTo(TypedValue o) throws Exception{
		o.type=Types.BOOLEAN;
		o.setB(value);
	}
}
