// Пара Имя-Значение
public class Token {
	public Terminal name;
	public String value;

	public Token(Terminal n, String v) {
		name = n;
		value = v;
	}
	
	@Override
	public String toString() {
		return "Token [name=" + name.toString() + ", value=" + value + "]";
	}
}
