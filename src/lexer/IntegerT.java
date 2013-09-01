package lexer;

import types.TypedValue;
import types.Types;



public class IntegerT extends Token{
	final public int value;
	public IntegerT(Tag n, int value) {
		super(n);
		this.value=value;
	}
	@Override
	public String toString() {
		return "" + value ;
	}

	public void getTypedValueTo(TypedValue o) throws Exception{
		o.type=Types.INTEGER;
		o.setI(value);
	}
}
