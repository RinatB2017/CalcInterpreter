package executables;

import interpretator.Voidable;
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
		//v = expr(true);
		table.put(name, v);
		output.addln("Значение переменой " + name + " изменено на " + v);
	}

}
