package types;

import java.util.ArrayList;

import exceptions.MyException;
import types.func.def.*;

public abstract class Function {
	protected Definition definition;
	protected Dimension dimensionFromOptions;
	
	// Здесь мы передаём список аргументов, полученных от пользователя, типы будут проверены в реализации
	public abstract TypedValue execute(ArrayList<TypedValue> args) throws Exception;
	
	public Function(ArrayList<Argument> args, Argument ret){
		definition=new Definition(args, ret);
	}
	
	@Override
	public String toString() {
		String r="";
		
		if(definition.ret!=null)
			r+= definition.ret.toString();
		
		r+= " (";
		
		if(definition.args!=null){
			if(definition.args.isEmpty())
				;
			else {
				r += definition.args.get(0);
				int i = 1;
				while(i!=definition.args.size()){
					r += ", " + definition.args.get(i);
					i++;
				}
			}
		}
		r += ")";
		return r;
	}

	/*public String getFuncArgs() {
		return definition.args.toString();
	}
	
	public String getFuncRet() {
		return definition.ret.toString();
	}
	*/
	
	// Проверка соответствия типов передаваемых аргументов и типов аргументов, указанных в определении
	protected void checkArguments(ArrayList<TypedValue> arguments) throws Exception{
		if(definition.args==null && arguments==null) return;
		if(definition.args==null)
			throw new MyException("Список аргументов должен быть пустым.");
		if(arguments==null)
			throw new MyException("Список аргументов должен быть непустым.");
		if(arguments.size()!=definition.args.size())
			throw new MyException("Неверное кол-во аргументов");
		for(int i=0; i<arguments.size(); i++){
			TypedValue arg = arguments.get(i);
			Types definedType = definition.getArg(i).type;
			if(arg.getType()!=definedType)
				//throw new MyException("Не совпадает тип аргумента функции со своим определением.");
				arg.tryMaximizeTo(definedType);
		}
	}
	
	protected void checkRet(TypedValue ret) throws Exception{
		if(definition.ret==null && ret==null) return;
		if(definition.ret==null)
			throw new MyException("Возвращаемое значение должно быть пустым.");
		if(ret==null)
			throw new MyException("Возвращаемое значение должно быть непустым.");
		if(ret.getType()!= definition.ret.type)
			throw new MyException("Не совпадает тип возвращаемого значения функции со своим определением.");
	}
	
	public void setDimension(Dimension dim) {
		dimensionFromOptions=dim;
	}
}
