package inter.returnables;

import inter.Returnable;
import main.MyException;
import options.OptId;
import types.TypedValue;

public class TableGet extends Returnable {
	private String name;
	
	public TableGet(String name) {
		this.name=name;
	}

	@Override
	public TypedValue execute() throws Exception {
		if (!table.containsKey(name)){
			if (options.getBoolean(OptId.STRICTED))
				throw new MyException("Запрещено автоматическое создание переменных в stricted-режиме");
			else {
				table.put(name, new TypedValue(0)); // Если в table нет переменной, то
				// добавляем её со зачением 0
				output.addln("Создана переменная " + name
						+ " со значением по умолчанию " + table.get(name));
			}
		}
		TypedValue r = table.get(name);
		//return new TypedValue(r);
		return r.clone();
	}

}
