package lexer;

import java.util.Calendar;
import java.util.Date;

import types.*;

public class DateT extends Token{
	final public Calendar value;
	
	public DateT(Tag n, Calendar value) {
		super(n);
		this.value=(Calendar) value.clone();
		// System.out.println("DateT constructor: " + value.get(Calendar.DAY_OF_MONTH) + " " + value.get(Calendar.MONTH) + " " + value.get(Calendar.YEAR));
	}
	
	@Override
	public String toString() {
		// Из-за того что в Java месяцы начинаются с (sic!) 0, мы прибавляем 1. Вычитаем в Lexer на строке 241
		return createString(value);
	}

	public void sendTypedValueTo(TypedValue o) throws Exception{
		o.setDate(value);
	}
	
	public static String createString(Calendar value){
		return new String(""+ value.get(Calendar.DAY_OF_MONTH) +"." + (value.get(Calendar.MONTH)+1) + "." + value.get(Calendar.YEAR) );
	}
}
