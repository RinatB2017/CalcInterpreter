package types.func;

import java.util.ArrayList;
import types.TypedValue;
import types.func.def.Argument;

public class BuiltInFunction extends Function {
	private final EnumOfBuiltInFunctions f;
	
	public BuiltInFunction(EnumOfBuiltInFunctions f, ArrayList <Argument> args, Argument ret){
		super(args, ret);
		this.f = f;
	}
	
	@Override
	public TypedValue execute(ArrayList <TypedValue> arguments) throws Exception {
		checkArguments(arguments);
		TypedValue ret=null;
		switch(f){
		case SIN:
			ret=new TypedValue(Math.sin(arguments.get(0).getDouble()));
			break;
		case COS:
			ret=new TypedValue(Math.cos(arguments.get(0).getDouble()));
			break;
		case ARCSIN:
			break;
		case ARCCOS:
			break;
		case TAN:
			ret=new TypedValue(Math.tan(arguments.get(0).getDouble()));
			break;
		case CTG:
			//ret=new TypedValue(Math.(arguments.get(0).getDouble()));
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
		return ret;
	}

}
