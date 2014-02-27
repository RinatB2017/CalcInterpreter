package inter.returnables;

import inter.Returnable;
import lexer.Tag;
import types.TypedValue;

public class Term extends Returnable {
	private Tag sign;
	
	public Term(TypedValue left, TypedValue right, Tag sign) throws Exception {
		super(left, right);
		this.sign = sign;
	}
	
	public TypedValue execute() throws Exception {
		switch (sign){
		case MUL:
			return left.mul(right);
		case DIV:
			return left.div(right);
		default:
			throw new Exception("неверный знак в Term");
		}
	}
	
}
