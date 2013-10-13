package inter.returnables;

import java.util.*;

import exceptions.MyException;
import types.Function;
import types.TypedValue;
import types.Types;
import inter.Returnable;

public class Func extends Returnable{
	private String name;
	private ArrayList<TypedValue> args;
	
	public Func(String name, ArrayList<TypedValue> args) {
		this.args=args;
		this.name=name;
	}

	// Посылает аргументы в функциональный объект и получает результат
	public TypedValue execute() throws Exception{
		TypedValue funcObj = table.get(name);
		if(funcObj==null) throw new MyException("Объекта с именем "+name+" нет в таблице!");
		if(funcObj.getType()!=Types.FUNCTION) throw new MyException("Объект с именем "+name+" не является функцией.");
		Function theFunction = funcObj.getFunction();
		
		// Выставляем временному объекту настройку(радианы, градусы) для конвертации в execute()
		theFunction.setDimension(options.getDimension());
		return theFunction.execute(args);
	}

}
