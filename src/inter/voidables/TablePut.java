package inter.voidables;

import inter.Voidable;
import types.TypedValue;

public class TablePut extends Voidable {
	private String name;
	private TypedValue v;
	public TablePut(String name, TypedValue v) throws Exception {
		if(name==null)
			throw new Exception();
		
		this.name=name;
		this.v=v;
	}

	@Override
	public void execute() throws Exception {
		if(v==null)
			throw new Exception("Пресечена попытка добавления переменной "+name+", сопоставленной с null.");
				
		table.put(name, v.clone());
		output.addln("(Пере)записана переменая " + name + " значением " + v);
	}
}
