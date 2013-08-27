package interpretator;

import lexer.Tag;
import main.MyException;
import types.TypedValue;

public class Term implements Returnable {
	private TypedValue left, right;
	private Tag sign;
	
	public Term(TypedValue left, TypedValue right, Tag sign) {
		this.left = left;
		this.right = right;
		this.sign = sign;
	}
	
	public TypedValue execute() throws Exception {
		switch (sign){
		case MUL:
			return left.mul(right);
		case DIV:
			return left.div(right);
		default:
			throw new MyException("неверный знак в Expr");
		}
	}
	
}
