// Пара Имя-Значение
public class Token {
	public Names name;
	public String value;

	public Token(Names n, String v) {
		name = n;
		value = v;
	}
	
	@Override
	public String toString() {
		return "Token [name=" + name.toString() + ", value=" + value + "]";
	}
}
