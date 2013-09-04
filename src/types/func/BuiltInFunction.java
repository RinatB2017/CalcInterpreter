package types.func;

import java.util.ArrayList;
import java.util.Collection;

import main.MyException;
import types.TypedValue;
import types.func.def.Argument;

public class BuiltInFunction extends Function {
	private final EnumOfBuiltInFunctions f;
	
	public BuiltInFunction(EnumOfBuiltInFunctions f, ArrayList <Argument> args, Argument ret){
		this.f = f;
		this.definition.args=args;
		this.definition.ret=ret;
	}
	
	@Override
	public TypedValue execute(ArrayList <ArgValue> arguments) throws MyException {
		checkArguments(arguments);
		TypedValue ret=null;
		switch(f){
		case SIN:
			ret=new TypedValue(Math.sin(arguments.get(0).value.getDouble()));
			break;
		case COS:
			break;
		case ARCSIN:
			break;
		case ARCCOS:
			break;
		case TAN:
			break;
		case CTG:
			break;
		case ARCTAN:
			break;
		case ARCTG:
			break;
		case LOG:
			break;	
		case POW:
			break;
		}
		checkRet(ret);
		return null;
	}

}
