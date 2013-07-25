package lexer;

public class WordT extends Token{
	public final String value;
	public WordT(Tag n, String v) {
		super(n);
		value=v;
	}
}
