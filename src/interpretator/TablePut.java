package interpretator;

import types.TypedValue;

public class TablePut implements Voidable {
	private Interpreter i; 
	private String name;
	private TypedValue v;
	public TablePut(String name, TypedValue v, Interpreter i) {
		this.name=name;
		this.i=i;
		this.v=v;
	}

	@Override
	public void execute() throws Exception {
		//v = expr(true);
		i.table.put(name, v);
		i.output.addln("Значение переменой " + name + " изменено на " + v);
	}

}
