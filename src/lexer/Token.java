package lexer;
// Пара Имя-Значение
public class Token {
	public Tag name;

	public Token(Tag n) {
		name = n;
	}
	
	@Override
	public String toString() {
		//return "Token[name=" + name.toString() + ", value=\"" + value + "\"]";
		return "Token[name=" + name.toString()+ "]";
	}
}
