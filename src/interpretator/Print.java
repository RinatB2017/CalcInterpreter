package interpretator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import main.OutputSystem;

import types.TypedValue;

public class Print implements Voidable {

	public Print(TypedValue v, Interpreter i) {
		super();
		this.v = v;
		this.output=i.output;
		this.table=i.table;
	}

	private TypedValue v;
	private OutputSystem output;
	private HashMap<String, TypedValue> table;
	
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
