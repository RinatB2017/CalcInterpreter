package lexer;

import java.util.Calendar;
import java.util.Date;

import types.*;

public class DateT extends Token{
	final public Date value;
	
	public DateT(Tag n, Date value) {
		super(n);
		this.value=value;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String toString() {
		//return String.valueOf(value);
		return new String(""+value.getYear() + "." + value.getMonth() + "." + value.getDay());
	}

	public void sendTypedValueTo(TypedValue o) throws Exception{
		o.setDate(value);
	}
}
