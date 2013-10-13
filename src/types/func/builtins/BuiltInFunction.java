package types.func.builtins;

import java.util.ArrayList;

import exceptions.UserWantsExit;
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
		
		/*
		 * Добавление функций в таблицу - в классе Reset
		 * */
		
		switch(f){
		case SIN:
			ret=new TypedValue(Math.sin(arguments.get(0).getDouble()));
			break;
		case COS:
			ret=new TypedValue(Math.cos(arguments.get(0).getDouble()));
			break;
		case ARCSIN:
			ret=new TypedValue(Math.asin(arguments.get(0).getDouble()));
			break;
		case ARCCOS:
			ret=new TypedValue(Math.acos(arguments.get(0).getDouble()));
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
		case EXITF:
			throw new UserWantsExit(arguments.get(0).getInt());
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

	// Преобразует в аргументы в размерность, указанную в defDim из получаемой по ключу OptId.DIMENSION
	private void convertArgumentsByDefinition(ArrayList<TypedValue> arguments) throws Exception{
		for(int i=0; i<arguments.size(); i++){
			convert(arguments.get(i), dimensionFromOptions, definition.args.get(i).getDimension());
		}
	}
	
	// Преобразовать выходное значение из указанной в определении выходного значения
	// в получаемую по ключу OptId.DIMENSION
	private void convertRetValueByDefinition(TypedValue ret) throws Exception{
		convert(ret, definition.ret.getDimension(), dimensionFromOptions);
	}
	
	private void convert(TypedValue v, Dimension from, Dimension to) throws Exception{
		checkNoDimensionless(dimensionFromOptions, true);

		switch(to){ // во что преобразовываем
		case DEG:
			toDeg(v, from);
			break;
		case RAD:
			toRad(v, from);
			break;
		default:
			break;
		}
	}
	
		
	// Преобразует в радианы
	private void toRad(TypedValue v, Dimension from) throws Exception {
		if(v.getType()!=Types.DOUBLE) return;
		
		switch(from){
		case DEG:
			v.setDouble(v.getDouble() * Math.PI / 180);
			break;
		default:
			// Ничего не преобразуем, если величина безразмерностная
			break;
		}
	}
	
	// Преобразует в градусы
	private void toDeg(TypedValue v, Dimension from) throws Exception{
		if(v.getType()!=Types.DOUBLE) return;
		
		switch(from){
		case RAD:
			v.setDouble(v.getDouble() * 180.0 / Math.PI);
			break;
		default:
			// Ничего не преобразуем, если величина безразмерностная
			break;
		}
	}
}
