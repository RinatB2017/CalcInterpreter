/**
 * Класс токен, представляющий собой пару 	
 * Имя : Значение
 * @author Ник
 */
public class Token {
	/**
	 * */
	public final Terminal name; // Запрет изменения поля после того как оно будет установлено
	public final String value;
	
	/**
	 * @param n Терминал / Имя токена / etc...
	 * @param v Строковая лексема / Значение
	 */
	public Token(Terminal n, String v) {
		name = n;
		value = v;
	}
	
	@Override
	public String toString() {
		return "Token[name=" + name.toString() + ", value=\"" + value + "\"]";
	}
}
