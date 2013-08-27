package interpretator;

import lexer.Tag;
import main.MyException;
import types.TypedValue;

public class Expr implements Returnable {
	private TypedValue left, right;
	private Tag sign;
	
	public Expr(TypedValue left, TypedValue right, Tag sign) {
		this.left = left;
		this.right = right;
		this.sign = sign;
	}
	
	public TypedValue execute() throws Exception {
		switch (sign){
		case PLUS:
			return left.plus(right);
		case MINUS:
			return left.minus(right);
		default:
			throw new MyException("неверный знак в Expr");
		}
	}
	
}
