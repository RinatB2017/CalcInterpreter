
// Пара Имя-Значение
public class Token {
	public final Tag name;
	public final String string;

	public Token(Tag n) {
		name = n;
		string=null;
	}
	
	public Token(Tag n, String s) {
		name = n;
		this.string=s;
	}
	
	@Override
	public String toString() {
		//return "Token[name=" + name.toString() + ", value=\"" + value + "\"]";
		return string;
	}
}
