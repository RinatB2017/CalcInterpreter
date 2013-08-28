package interpretator;

import main.MyException;
import options.OptId;
import types.TypedValue;

public class TableGet implements Returnable {

	private Interpreter i; 
	private String name;
	
	public TableGet(String name, Interpreter i) {
		this.i=i;
		this.name=name;
	}

	@Override
	public TypedValue execute() throws Exception {
		if (!i.table.containsKey(name)){
			if (i.options.getBoolean(OptId.STRICTED))
				throw new MyException("Запрещено автоматическое создание переменных в stricted-режиме");
			else {
				i.table.put(name, new TypedValue(0)); // Если в table нет переменной, то
				// добавляем её со зачением 0
				i.output.addln("Создана переменная " + name
						+ " со значением " + i.table.get(name));
			}
		}
		TypedValue r = i.table.get(name);
		return r;
	}

}
