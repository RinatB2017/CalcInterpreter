package lexer;



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

}
