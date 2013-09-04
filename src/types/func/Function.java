package types.func;

import java.util.ArrayList;

import main.MyException;
import types.TypedValue;
import types.func.def.*;

public abstract class Function {
	protected Definition definition;
	public abstract TypedValue execute(ArrayList<TypedValue> args) throws Exception;
	
	public Function(ArrayList<Argument> args, Argument ret){
		definition=new Definition(args, ret);
	}
	
	protected void checkArguments(ArrayList<TypedValue> arguments) throws Exception{
		if(arguments==null)
			throw new Exception("args==null");
		if(arguments.size()!=definition.args.size())
			throw new MyException("Неверное кол-во аргументов");
		for(int i=0; i<arguments.size(); i++){
			if(arguments.get(i).type!=definition.getArg(i).type)
				throw new MyException("Не совпадает тип аргумента функции со своим определением.");
		}
	}
	
	protected void checkRet(TypedValue ret) throws Exception{
		if(ret==null)
			throw new Exception("ret==null");
		if(ret.type!= definition.ret.type)
			throw new MyException("Не совпадает тип аргумента функции со своим определением.");
	}
}
