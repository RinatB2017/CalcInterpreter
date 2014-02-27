package inter.returnables;

import inter.Returnable;
import lexer.Tag;
import types.TypedValue;

public class Expr extends Returnable {
	private Tag sign;
	
	public Expr(TypedValue left, TypedValue right, Tag sign) throws Exception {
		super(left, right);
		this.sign = sign;
	}
	
	public TypedValue execute() throws Exception {
		switch (sign){
		case PLUS:
			return left.plus(right);
		case MINUS:
			return left.minus(right);
		default:
			throw new Exception("неверный знак в Expr");
		}
	}
	
}
