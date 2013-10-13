package inter.voidables;

import inter.Voidable;

import java.util.Iterator;
import java.util.Map.Entry;

import types.*;

public class Print extends Voidable {

	public Print(TypedValue v) {
		this.v = v;
	}

	private TypedValue v;
	
	@Override
	public void execute() throws Exception {
		if(v==null){
			if (table.isEmpty()) {
				output.addln("Таблица переменных пуста, нечего показывать.");
			} else {
				output.addln("[table]");
				Iterator<Entry<String, TypedValue>> it = table.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, TypedValue> li = it.next();
					
					output.addln("" + li.getKey() + " " + li.getValue().toStringForPrintTable());
				}
				output.addln("[/table]");
			}
		}else{
			output.finishAppend("= " + v);
		}

	}

}
