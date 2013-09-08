package inter.returnables;

import java.util.*;

import main.MyException;
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
		if(funcObj.type!=Types.FUNCTION) throw new MyException("Объект с именем "+name+" не является функцией.");
		
		return funcObj.getFunction().execute(args);
	}

}
