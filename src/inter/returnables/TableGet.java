package inter.returnables;

import exceptions.MyException;
import inter.Returnable;
import options.OptId;
import types.TypedValue;
import types.Types;

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
				// Если в table нет переменной, то добавляем её со зачением 0
				table.put(name, new TypedValue(0));
				output.addln("Создана переменная " + name
						+ " со значением по умолчанию " + table.get(name));
			}
		}
		TypedValue r = table.get(name);
		
		if(r.getType()==Types.FUNCTION)
			throw new MyException("При обращении к функции нужно указывать скобки.");
		
		return r.clone();
	}

}
