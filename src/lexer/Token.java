package lexer;

import types.TypedValue;

// Пара Имя-Значение
public class Token {
	public final Tag tag;
	final String string;

	public Token(Tag n) {
		tag = n;
		string=null;
	}
	
	public Token(Tag n, String s) {
		tag = n;
		this.string=s;
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	public String toStringWithName() {
		return "'"+tag.toString()+"' \""+toString()+"\"";
	}
	
	public void getTypedValueTo(TypedValue o) throws Exception{ // поддерживается только в наследниках, которых должен сгенерировать лексер
		throw new UnsupportedOperationException(); 
	}
}
