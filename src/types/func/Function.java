package types.func;

import java.util.ArrayList;

import main.MyException;
import types.TypedValue;
import types.func.def.Argument;
import types.func.def.Definition;

public abstract class Function {
	protected Definition definition;
	public abstract TypedValue execute(ArrayList <ArgValue> arguments) throws MyException;
	
	protected void checkArguments(ArrayList<ArgValue> arguments) throws MyException{
		if(arguments==null)
			throw new MyException("args==null");
		for(int i=0; i<arguments.size(); i++){
			if(!arguments.get(i).equals(definition.getArg(i)))
				throw new MyException("Не совпадает тип аргумента функции со своим определением.");
		}
	}
	
	protected void checkRet(TypedValue ret) throws MyException{
		if(ret==null)
			throw new MyException("ret==null");
		if(!ret.equals(definition.ret))
			throw new MyException("Не совпадает тип аргумента функции со своим определением.");
	}
}
