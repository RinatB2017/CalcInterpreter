package executables;

import java.util.*;

import main.MyException;
import types.TypedValue;
import types.Types;
import interpreter.Returnable;

public class Func extends Returnable{
	private String name;
	private ArrayList<TypedValue> args;
	
	public Func(String name, ArrayList<TypedValue> args) {
		this.args=args;
		this.name=name;
	}

	// Посылает аргументы в функциональный объект и получает результат
	public TypedValue execute() throws MyException{
		TypedValue funcObj = table.get(name);
		if(funcObj.type!=Types.FUNCTION) throw new MyException("Объект с именем "+name+" не является функцией.");
		
		System.out.print("calling "+name+"(");
		if(args!=null)
			for (TypedValue t : args){
				System.out.print(t+", ");
			}
		
		System.out.println(")");
		return new TypedValue(1338);
	}

}
