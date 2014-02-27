package types.func.userdefined;

import java.util.ArrayList;

import types.*;
import types.func.def.Argument;

public class FuncPrim extends Function{
	public FuncPrim(ArrayList<Argument> args, Argument ret) {
		super(args, ret);
	}

	String name=null; // имя ф-ии или переменной
	
	TypedValue constant=null; // константа или объект функционального типа, который по сути тоже является константой, как например 2.
	// Сумма, например, объектов функционального типа даёт новый объект функционального типа
	
	FuncPrimType type;
	
	/*boolean equals(){
		return false;
	}*/

	@Override
	public TypedValue execute(ArrayList<TypedValue> arguments) {
		return null;
	}
}
