package types.func.builtins;

import java.util.ArrayList;

import types.Function;
import types.TypedValue;
import types.Types;
import types.func.def.Argument;
import types.func.def.Dimension;
import static types.func.def.Dimension.checkNoDimensionless;

public class BuiltInFunction extends Function {
	private final EnumOfBuiltInFunctions f;
	
	public BuiltInFunction(EnumOfBuiltInFunctions f, ArrayList <Argument> args, Argument ret){
		super(args, ret);
		this.f = f;
	}
	
	@Override
	public TypedValue execute(ArrayList <TypedValue> arguments) throws Exception {
		// Проверка соответствия типов передаваемых аргументов и типов аргументов, указанных в определении
		checkArguments(arguments);
		
		// Преобразовать радианы в градусы и т. д. в зависимости от опций
		if(isTrigFunc())
			convertArgumentsByDefinition(arguments);
		
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
		
		if(isTrigFunc())
			convertRetValueByDefinition(ret);
		return ret;
	}
	
	boolean isTrigFunc(){
		switch(f){
		case SIN:
		case COS:
		case ARCSIN:
		case ARCCOS:
		case TAN:
		case CTG:
		case ARCTAN:
		case ARCTG:
			return true;
		default:
			return false;
		}
	}

	// Преобразовать радианы в градусы и т. д. в зависимости от опций
	private void convertArgumentsByDefinition(ArrayList<TypedValue> arguments) throws Exception{
		for(int i=0; i<arguments.size(); i++){
			toDim(arguments.get(i), definition.args.get(i).getDimension());
		}
	}
		
	private void convertRetValueByDefinition(TypedValue ret) throws Exception{
		toDim(ret,definition.ret.getDimension());
	}
	
	private void toDim(TypedValue v, Dimension defDim) throws Exception{
		checkNoDimensionless(dimensionFromOptions, true);
		
		switch(dimensionFromOptions){
		case DEG:
			toDeg(v, defDim);
			break;
		case RAD:
			toRad(v, defDim);
			break;
		default:
			break;
		}
	}
	
	private void toRad(TypedValue v, Dimension defDim) throws Exception {
		if(v.getType()!=Types.DOUBLE) return;
		switch(defDim){
		case DEG:
			// x радиан = 
			v.setDouble(180 * v.getDouble() / Math.PI);
			break;
		default:
			// Ничего не преобразуем, если величина безразмерностная
			break;
		}
	}

	private void toDeg(TypedValue v, Dimension defDim) throws Exception{
		if(v.getType()!=Types.DOUBLE) return;
		switch(defDim){
		case RAD:
			// x градус =
			v.setDouble(Math.PI * v.getDouble() / 180);
			break;
		default:
			// Ничего не преобразуем, если величина безразмерностная
			break;
		}
	}
}
