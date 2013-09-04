package types.func;

import java.util.ArrayList;

import main.MyException;
import types.TypedValue;
import types.func.def.*;

public abstract class Function {
	protected Definition definition;
	public abstract TypedValue execute(ArrayList <ArgValue> arguments) throws Exception;
	
	protected void checkArguments(ArrayList<ArgValue> arguments) throws Exception{
		if(arguments==null)
			throw new Exception("args==null");
		if(arguments.size()!=definition.args.size())
			throw new MyException("Неверное кол-во аргументов");
		for(int i=0; i<arguments.size(); i++){
			if(!arguments.get(i).equals(definition.getArg(i)))
				throw new MyException("Не совпадает тип аргумента функции со своим определением.");
		}
	}
	
	protected void checkRet(TypedValue ret) throws Exception{
		if(ret==null)
			throw new Exception("ret==null");
		if(!ret.equals(definition.ret))
			throw new MyException("Не совпадает тип аргумента функции со своим определением.");
	}
}
