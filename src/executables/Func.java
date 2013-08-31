package executables;

import java.util.LinkedList;

import types.TypedValue;
import interpreter.Returnable;

public class Func extends Returnable{
	private String name;
	private LinkedList<TypedValue> args;
	
	public Func(String name, LinkedList<TypedValue> args) {
		this.args=args;
		this.name=name;
	}

	public TypedValue execute(){
		System.out.print("calling "+name+"(");
		if(args!=null)
			for (TypedValue t : args){
				System.out.print(t+", ");
			}
		
		System.out.println(")");
		return new TypedValue(1338);
	}

}
