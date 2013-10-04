package inter.voidables;

import inter.Voidable;
import types.TypedValue;

public class TablePut extends Voidable {
	private String name;
	private TypedValue v;
	public TablePut(String name, TypedValue v) {
		this.name=name;
		this.v=v;
	}

	@Override
	public void execute() throws Exception {
		table.put(name, v);
		output.addln("(Пере)записана переменая " + name + " значением " + v);
	}
}
