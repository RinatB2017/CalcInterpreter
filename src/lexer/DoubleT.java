package lexer;

import types.TypedValue;
import types.Types;



public class DoubleT extends Token{
	public final double value;
	public DoubleT(Tag name, double value) {
		super(name);
		this.value=value;
	}
	@Override
	public String toString() {
		return "" + value;
	}

	public void getTypedValueTo(TypedValue o) throws Exception{
		o.type=Types.DOUBLE;
		o.setD(value);
	}
}
