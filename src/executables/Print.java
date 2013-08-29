package executables;

import interpretator.Voidable;

import java.util.Iterator;
import java.util.Map.Entry;

import types.TypedValue;

public class Print extends Voidable {

	public Print(TypedValue v) {
		this.v = v;
	}

	private TypedValue v;
	
	@Override
	public void execute() throws Exception {
		if(v==null){
			if (table.isEmpty()) {
				output.addln("table is empty!");
			} else {
				output.addln("[table]");
				Iterator<Entry<String, TypedValue>> it = table.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, TypedValue> li = it.next();
					output.addln("" + li.getKey() + " " + li.getValue());
				}
				output.addln("[/table]");
			}
		}else{
			output.finishAppend("= " + v);
		}

	}

}
